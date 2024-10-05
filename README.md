# **AWS S3 이미지 업로드 및 관리 API**

## **프로젝트 설명**
이 프로젝트는 AWS S3를 이용하여 이미지 파일을 업로드, 조회, 삭제할 수 있는 기능을 제공하는 API 서버입니다.

## **목차**
1. [프로젝트 구조](#프로젝트-구조)
2. [API 사용 방법](#api-사용-방법)
3. [AWS 설정](#aws-설정)
4. [환결 설정](#환경-설정)
5. [빌드 및 실행 방법](#빌드-및-실행-방법)
6. [테스트 방법](#테스트-방법)


# **프로젝트 구조**

```
    project-directory/
    │
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── greed/
    │   │   │       ├── application/
    │   │   │       │   ├── controllers/ API 엔드포인트 관련 디렉토리
    │   │   │       │   └── exceptions/ 
    │   │   │       ├── common/  
    │   │   │       │   └── enums/ 
    │   │   │       ├── configuration/ AWS 설정 및 S3 클라이언트 설정 관련 디렉토리
    │   │   │       ├── domain/
    │   │   │       │   ├── dtos/ 요청 및 응답을 위한 DTO 디렉토리   
    │   │   │       │   └── services/ 이미지 관련 로직을 포함한 서비스 디렉토리 
    │   │   │       └── util/ 공통적으로 사용하는 로직을 모아둔 디렉토리 
    │   │   └── resources/
    │   │       └── application.properties   AWS 설정 및 Spring Boot 설정
    │   └── test/   유닛 테스트 코드
    ├── build.gradle 빌드 설정
    ├── README.md   프로젝트 문서
```

<div style="page-break-after: always;"></div>

## **디렉토리 설명**

- `application`
    - **controllers**: 클라이언트로부터 들어오는 요청을 처리하고 응답하는 컨트롤러들이 위치합니다. 각 API 엔드포인트는 이곳에 정의됩니다.
    - **exceptions**: 애플리케이션에서 발생할 수 있는 예외를 처리하는 사용자 정의 예외 클래스들이 포함됩니다.

- `common`
    - **enums**: 애플리케이션에서 공통적으로 사용하는 열거형 타입을 정의하는 디렉토리입니다. 예를 들어, 파일 형식과 같은 상수 값을 열거형으로 정의합니다.

- `domain`
    - **dtos**: 클라이언트와 서버 간의 데이터 전송을 담당하는 객체들이 포함됩니다. 이곳에 DTO(Data Transfer Object) 클래스들이 위치합니다.
    - **services**: 애플리케이션의 비즈니스 로직을 처리하는 서비스 클래스들이 포함됩니다. 서비스 레이어는 데이터 처리, 파일 저장, 삭제 등의 핵심 기능을 수행합니다.


# **API 사용 방법**

## **1.  이미지 업로드 API**
- URL : `/api/images/upload`
- Method : `POST`
- Request Param 
    - file : 사용자가 업로드할 이미지 파일
    - member-id : 이미지와 연결된 사용자 ID
    - path : 저장 경로
- Response 
    - 성공 시 :
  ```
  {
    "filename": "uploaded_image.jpg",
    "path": "user_uploads",
    "date":"2024-10-05 20:59:48"
  }
  ``` 
    - 실패 시 : 400 (파일 크기 초과 및 파일 확장자 오류)

<div style="page-break-after: always;"></div>

## **2. 이미지 조회 API**
- URL : `/api/images`
- Method : `GET`
- Response
    - 성공 시 :
  ```
   "images":[
              {
                "filename":"1234/user_uploads/bird.jpg",
                "url":"https://hanjinbucket.s3.ap-northeast-2.amazonaws.com/1234/user_uploads/bird.jpg",
                "uploadDate":"2024-10-05 18:53:29"
              }
            ]
  ``` 
    - 실패 시 : 조회에 실패해도 빈 리스트로 반환

## **3. 이미지 삭제 API**
- URL : `/api/images`
- Method : `DELETE`
- Response : 삭제 성공 또는 실패 여부 JSON 형태로 반환
    - 성공 시 :
  ```
  {
    "filename":"user_uploads/bird.jpg",
    "success":true,
    "message":"success"
  }
  ``` 
    - 실패 시 : 400 (파일 이름 누락 및 사용자 ID 오류)
  ```
  {
    "filename":" ",
    "success":false,
    "message":"Filename is required or ID value is invalid."
  }
  ```

<div style="page-break-after: always;"></div>

# **AWS 설정**
## **1.AWS S3 버킷 생성**
- AWS 콘솔에서 S3 버킷을 생성하고, 이미지 파일을 업로드할 수 있도록 권한을 부여
- 생성할 때 버킷 이름, 리전 정보, 엑세스 키, 시크릿 키를 확인 및 저장
    ```
    application.bucket.name=[버킷 이름]
    cloud.aws.s3.bucket=[버킷 이름]
    cloud.aws.region.static=ap-[리전 정보]
    cloud.aws.credentials.accessKey=[엑세스 키]
    cloud.aws.credentials.secretKey=[시크릿 키]
    ```
## **2. IAM 사용자 생성**
- S3 접근을 위한 IAM 사용자를 생성하고, 아래의 권한 부여
    ```
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "s3:PutObject",
                    "s3:GetObject",
                    "s3:DeleteObject"
                ],
                "Resource": "arn:aws:s3:::[버킷 이름]/*"
            },
            {
                "Effect": "Allow",
                "Action": "s3:ListBucket",
                "Resource": "arn:aws:s3:::[버킷 이름]"
            }
        ]
    }
    ```
- `s3:PutObject`,`s3:GetObject`,`s3:DeleteObject` 을 이용하여 S3 버킷에 최소한의 권한을 부여한다.
- `Resource` 해당 버킷 안의 모든 파일과 경로를 대상으로 지정된 액션을 허용
- `ListBucket` 권한을 사용하여 Amazon S3 버킷에서 객체 목록을 조회할 수 있는 권한을 허용

<div style="page-break-after: always;"></div>

## **3. 환경 변수 또는 application.properties 설정**
- src/main/resources/application.properties에 다음과 같이 AWS 자격 증명을 추가
    ```
    application.bucket.name=[버킷 이름]
    cloud.aws.s3.bucket=[버킷 이름]
    cloud.aws.stack.auto=false
    cloud.aws.region.static=[리전 정보]
    cloud.aws.credentials.accessKey=[엑세스 키]
    cloud.aws.credentials.secretKey=[비밀 키]
    ```
  
# **환경 설정**
- Java Version: 17 
- Spring Boot Version: 3.0.0
- Gradle 8.8
- AWS SDK for S3


# **빌드 및 실행 방법**
## **1. 의존성 설치 및 테스트 실행**
```
.\gradlew.bat clean build
```
  - 만약 Build 중 Test 가 성공하지 않는다면 `src/main/resources/application.properties`에 다음과 같이 AWS 자격 증명을 올바르게 추가 하였는지 확인 해주세요.
## **2. 애플리케이션 실행**
```
.\gradlew.bat bootRun
```
  - 만약 실행이되지 않는다면 8980 포트가 다른 프로세스에서 사용하고 있는지 확인해 주세요.

<div style="page-break-after: always;"></div>

# **테스트 방법**
- 이 프로젝트에서는 JUnit을 사용하여 각 API의 자동화된 테스트를 구현하고 있습니다. 각 API의 유효성을 검사하기 위해 다음과 같은 테스트 방법을 적용하고 있습니다.
- 프로젝트를 빌드하는 과정에서 자동으로 테스트가 실행됩니다. 하지만 테스트 결과만 보고 싶으시다면, 아래의 명령어를 사용하여 실행해 주세요.
  ```
  .\gradlew.bat test --tests "greed.application.controllers.ImageControllerTest" 
  ```
## **1. 이미지 업로드 API 테스트**
  - 목적: 사용자가 이미지를 업로드할 수 있는지 확인합니다.
  - 방법: MockMvc를 사용하여 이미지 파일을 포함한 multipart 요청을 보내고, 응답 상태가 200 OK인지 검증합니다.
  - 유효성 검사:
    - `notExtension, extensionCheck` : 지원되는 이미지 포맷(JPEG, PNG, GIF)이 아닌 파일을 업로드할 경우 400 BAD REQUEST 응답을 검증합니다.
    - `fileSizeExceedCheck` : 이미지 파일의 크기 제한(5MB)을 초과하는 경우 400 BAD REQUEST 응답을 검증합니다.

## **2. 이미지 조회 API 테스트**
  - 목적: S3 버킷에 저장된 모든 이미지의 정보를 반환하는 API를 확인합니다.
  - GET 요청을 보내고, 응답의 JSON 형태에서 이미지 URL, 파일명, 업로드 날짜 및 시간을 가져옵니다.
  - 유효성 검사:
    - 응답 상태가 200 OK인지 확인합니다.
    - 만약 s3 버킷 비어있거나 정보를 못가져왔을 때는 빈 리스트를 반환합니다.

## **3. 이미지 삭제 API 테스트**
  - 목적: 특정 파일을 S3 버킷에서 삭제할 수 있는지 확인합니다.
  - 파일명을 포함한 DELETE 요청을 보내고, 응답 상태가 200 OK인지 검증합니다.
  - 삭제에 실패 했으면 응답 상태가 400 성공 하면 200으로 반환합니다.
  - 유효성 검사:
    - `blankByDelete, memberIdErrorByDelete` : 사용자 id 및 파일 이름 유효성 검사에 실패 한 경우 400 BAD REQUEST 응답을 검증합니다.
    - 접근하는 계정에 삭제 권한이 없으면 400 BAD REQUEST 응답을 검증합니다.
