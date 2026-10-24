from flask import Flask, request, jsonify
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import firebase_admin
from firebase_admin import credentials, firestore

from flask_cors import CORS

# Firebase 서비스 계정 인증
cred = credentials.Certificate("C:\json_file_root\dyf24-39a7d-firebase-adminsdk.json")  # JSON 파일 경로
firebase_admin.initialize_app(cred)

# Firestore 클라이언트 초기화
db = firestore.client()

# Flask 애플리케이션 객체 생성
app = Flask(__name__)
@app.route('/')
def index():
    return "Welcome to the search and crawl API!"


# CORS 설정
CORS(app)

@app.route('/search_and_crawl', methods=['POST'])
def search_and_crawl():
    search_keyword = request.json.get('keyword')  # 요청에서 키워드 추출 #Androidstudio에서 받은 키워드
    print(f"받은 키워드: {search_keyword}")  # 터미널에 출력
    driver = webdriver.Chrome()

    # 사이트 열기
    driver.get("https://www.all-con.co.kr/")
    
    # 크롤링 코드 실행
    data_list = crawl_and_save_to_firestore(driver, search_keyword)
    
    driver.quit()
    
    return jsonify({"message": "Data crawled and saved successfully.", "data": data_list})

def crawl_and_save_to_firestore(driver, search_keyword):
    """크롤링 및 Firestore에 데이터 저장"""
    # 검색 입력창에 키워드 입력
    search_input = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "sch_stx"))
    )
    search_input.send_keys(search_keyword)

    # 검색 버튼 클릭
    search_button = driver.find_element(By.ID, "sch_submit")
    search_button.click()

    # 검색 결과 로드 대기
    WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.TAG_NAME, "tr")))

    # 데이터 수집
    data_list = []
    while True:
        data_list.extend(scroll_and_collect(driver))  # 현재 페이지 데이터 수집
        if not go_to_next_page(driver):  # 다음 페이지로 이동
            print("3페이지에 도달하거나 마지막 페이지입니다.")
            break

    # Firestore에 저장
    save_to_firestore(data_list)
    
    return data_list

def scroll_and_collect(driver):
    """현재 페이지에서 데이터를 수집하고 스크롤을 진행"""
    data_list = []
    while True:
        rows = driver.find_elements(By.XPATH, "//tr")
        for row in rows:
            try:
                type_ = row.find_element(By.CLASS_NAME, "type").text.strip()
                title = row.find_element(By.CLASS_NAME, "title").text.strip()
                link = row.find_element(By.CSS_SELECTOR, ".title a").get_attribute("href")
                host = row.find_element(By.CLASS_NAME, "host").text.strip()
                date = row.find_element(By.CLASS_NAME, "date").text.strip()
                status = row.find_element(By.CLASS_NAME, "status").text.strip()

                entry = {
                    "type": type_,
                    "title": title,
                    "host": host,
                    "period": date,
                    "status": status,
                    "link": link,
                }
                data_list.append(entry)
            except Exception:
                continue

        # 페이지 스크롤
        prev_height = driver.execute_script("return document.body.scrollHeight")
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(2)

        curr_height = driver.execute_script("return document.body.scrollHeight")
        if curr_height == prev_height:
            break

    return data_list

def go_to_next_page(driver):
    """다음 페이지로 이동"""
    try:
        current_page = driver.find_element(By.XPATH, "//span[contains(@class, 'page-current')]")
        current_page_number = int(current_page.get_attribute("data-page"))

        if current_page_number >= 3:
            return False

        next_page = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.XPATH, f"//span[@data-page='{current_page_number + 1}']"))
        )
        next_page.click()
        time.sleep(3)
        return True
    except Exception as e:
        print(f"다음 페이지로 이동 중 오류 발생: {e}")
        return False

def save_to_firestore(data_list):
    """Firestore에 데이터를 저장 (기존 데이터 삭제 후 새 데이터 추가)"""
    collection_name = "search_result"
    
    # 기존 데이터 삭제
    try:
        docs = db.collection(collection_name).stream()
        for doc in docs:
            doc.reference.delete()
        print("기존 데이터 삭제 완료.")
    except Exception as e:
        print(f"기존 데이터 삭제 오류: {e}")
    
    # 새 데이터 저장
    for data in data_list:
        try:
            db.collection(collection_name).add(data)
            print(f"Firestore에 새 데이터 저장 완료: {data['title']}")
        except Exception as e:
            print(f"Firestore 저장 오류: {e}")

# Firestore에서 데이터를 가져오는 API 추가
@app.route('/get_data', methods=['GET'])
def get_data():
    # Firestore에서 데이터 읽기
    collection_name = "search_result"
    docs = db.collection(collection_name).stream()
    
    # 데이터를 리스트로 변환
    data_list = []
    for doc in docs:
        data_list.append(doc.to_dict())
    
    return jsonify({"data": data_list})

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, ssl_context=('cert.pem', 'key.pem'))
