import time
from selenium import webdriver
from selenium.webdriver.common.by import By
import firebase_admin
from firebase_admin import credentials, firestore

# Firebase 서비스 계정 인증
cred = credentials.Certificate("C:\json_file_root\dyf24-firebase-adminsdk.json")  # JSON 파일 경로
firebase_admin.initialize_app(cred)

# Firestore 클라이언트 초기화
db = firestore.client()

# 크롬 드라이버 경로 설정
driver_path = "/path/to/chromedriver"  # 크롬 드라이버 경로 설정

def scroll_and_collect(driver):
    data_list = []
    while True:
        rows = driver.find_elements(By.XPATH, "//tr")
        for row in rows:
            try:
                # 제목 추출
                title_element = row.find_element(By.XPATH, ".//a")
                title = title_element.text.strip()

                # 링크 추출
                link = title_element.get_attribute("href")

                # 기간 추출
                period = row.find_element(By.XPATH, ".//td[contains(@class, 'date')]").text.strip()

                # 회사명 추출
                company = row.find_element(By.XPATH, ".//td[contains(@class, 'company')]").text.strip()

                # 남은 시간 추출
                d_day = row.find_element(By.XPATH, ".//td[contains(@class, 'd-day clearfix')]").text.strip()

                # 리스트에 데이터 추가
                entry = {
                    "title": title,
                    "period": period,
                    "host": company,
                    "status": d_day,
                    "link": link
                }
                data_list.append(entry)
            except Exception:
                continue

        # 페이지 스크롤
        prev_height = driver.execute_script("return document.body.scrollHeight")
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(2)  # 스크롤 후 대기

        # 스크롤 후 높이 확인
        curr_height = driver.execute_script("return document.body.scrollHeight")
        if curr_height == prev_height:
            break  # 더 이상 스크롤되지 않으면 종료

    return data_list


def go_to_next_page(driver):
    try:
        # 현재 페이지 번호 확인
        current_page = driver.find_element(By.XPATH, "//span[contains(@class, 'page-current')]")
        current_page_number = int(current_page.get_attribute("data-page"))

        # 3페이지까지만 실행 (필요시 3 페이지로 수정)
        if current_page_number >= 3:
            return False

        # 다음 페이지 버튼 클릭
        next_page = driver.find_element(By.XPATH, f"//span[@data-page='{current_page_number + 1}']")
        next_page.click()
        time.sleep(3)  # 페이지 로드 대기
        return True
    except Exception as e:
        print(f"다음 페이지로 이동 중 오류 발생: {e}")
        return False  # 더 이상 다음 페이지가 없으면 종료


def save_to_firestore(data_list):
    collection_name = "New activity in recent times"  # 저장할 컬렉션 이름
    for data in data_list:
        try:
            # title 또는 link를 기준으로 중복 체크
            existing_doc = db.collection(collection_name).where("link", "==", data["link"]).get()

            if existing_doc:
                # 중복된 데이터가 있으면 업데이트
                doc_id = existing_doc[0].id
                db.collection(collection_name).document(doc_id).set(data)  # 문서 갱신
                print(f"Firestore에 데이터 갱신 완료: {data['title']}")
            else:
                # 중복된 데이터가 없으면 새로 추가
                db.collection(collection_name).add(data)
                print(f"Firestore에 새 데이터 저장 완료: {data['title']}")
        except Exception as e:
            print(f"Firestore 저장 오류: {e}")


# 반복적으로 크롤링 및 데이터 갱신
while True:
    driver = webdriver.Chrome()
    url = "https://www.all-con.co.kr/"
    driver.get(url)

    # 첫 페이지에서 데이터 수집 및 페이지 이동 반복
    data_list = []
    while True:
        data_list.extend(scroll_and_collect(driver))  # 현재 페이지 데이터 수집
        if not go_to_next_page(driver):  # 다음 페이지로 이동
            print("3페이지에 도달하거나 마지막 페이지입니다.")
            break

    # 데이터 DB에 저장
    save_to_firestore(data_list)

    # 드라이버 종료
    driver.quit()

    # 10분 기다린 후, 반복
    print("1시간 대기 후, 크롤링을 다시 시작합니다.")
    time.sleep(3600)  # 600초 = 10분, 3600초 = 60분 
