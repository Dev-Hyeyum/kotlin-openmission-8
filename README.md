# kotlin-openmission-8
## 🎨 어울림(Eoullim): 안드로이드에서 블록 기반의 실시간 웹 디자인 서비스

## 📑목차
0. [🎬 서비스 실행 화면](#heading-0)
1. [🚀 프로젝트 소개](#heading-1)
2. [📦 의존성](#heading-2)
3. [🧪 기능 목록](#3--기능-목록)
   
	3.1 [⚡ 실시간 동기화](#31--사용자의-상호작용에-따른-앱---웹으로의-즉각적인-동기화)
   
	3.2 [🞧 컴포넌트 생성](#32--웹-컴포넌트-생성)
   
 	3.3 [🖌️ 컴포넌트 커스텀](#33-️-웹-컴포넌트-커스텀-기능)
   
	3.4 [🖱️ 드래그/선택](#34-️-컴포넌트-선택-드래그-기능)
   
	3.5 [🧱 Layer 설정](#35--웹-컴포넌트의-layer-level-설정-가능)
   
	3.6 [📁 멀티 캔버스](#36--멀티-캔버스-기능)
   
	3.7 [🔗 웹페이지 제공](#36--사용자가-만든-웹페이지-링크-제공)
   
5. [▶️🔧 실행 방법](#4-️-실행-방법)
6. [🏗️ 아키텍처](#5-️-시스템-아키텍처)
7. [📄 라이선스](#6--라이선스)

## <a id="heading-0"></a>0. 🎬 서비스 실행 화면
- 어울림 서비스로 구현해본 간단한 로그인 화면 프로토타입 입니다!
  
http://kotlin-openmission.myftp.org:9090/test.html?room=947fda

## <a id="heading-1"></a>1. 🚀 프로젝트 소개
### 🧑‍💻 Team Dev-Hyeyum

|<img src="https://github.com/user-attachments/assets/f54da00e-f0dd-49b0-ac7f-36d17e91d0c3" width="150" height="150"/>|<img src="https://github.com/user-attachments/assets/bab9b945-1da5-4f6d-8b8c-7bee1ca8cee8" width="150" height="150"/>|
|:-:|:-:|
|김조현 (Kim Jo Hyeon)<br/>[@NoeyhOj](https://github.com/NoeyhOj)|이현우 (Lee Hyeon woo)<br/>[@cbnuLeehyunwoo](https://github.com/cbnuLeehyunwoo)|Git<br/>[@git](https://github.com/git)|

### 1.1. 📝 개요
- 우아한 테크코스 4~5주차 오픈미션을 수행하는 프로젝트입니다.
### 1.2. 🧩 도전
- 익숙하지 않은 기술 사용하기
    - Ktor 프레임워크
    - Jetpack Compose
    - SDUI(Server Driven User Interface)
    - Websocket
- 협업을 통해 프로젝트 진행하기,
- 담당한 부분(앱, 웹, 서버)을 주기적으로 상호교환하면서 익숙치 않은 기술과 환경 계속 마주하기
- 최대한 완벽하게 서비스 구현해보기
### 1.3. 🎯 핵심 목표
- Ktor를 사용한 백엔드 API 및 WebSocket 서버 구축
- Jetpack Compose를 사용한 반응형 안드로이드 UI 구현
- 안드로이드 앱 <-> Ktor서버 <-> 웹 간의 실시간 연동 구현
- 웹 컴포넌트 커스텀 기능  

## <a id="heading-2"></a>2. 📦 의존성

### 📱 모바일 앱(안드로이드)
``` bash
# Language & Core

* Language: Kotlin 2.2.21

# UI Framework (Jetpack Compose BOM 2024.09.00)

* Material3: 1.4.0
* Component: ColorPicker Compose 1.1.2
* UX: Core Splashscreen 1.0.1

# Architecture & Network

* Navigation: Navigation Compose 2.9.6
* Network: Ktor Client 3.3.1 (CIO Engine)
* Image Loading: Coil 3.3.0
* Async: Coroutines, Flow
```

### 🖥️ 백엔드 서버(Ktor Server)
	
``` bash
# Core Framework

* Framework: Ktor Server 3.3.1
* Engine: Netty

# Key Features

* Real-time: WebSockets
* Error Handling: Status Pages
* Data Format: Content Negotiation (JSON)

# Utilities

* Serialization: Kotlinx Serialization
* Logging: Logback Classic 1.5.6
```
### 🧰 개발 도구 및 인프라
	
```bash
# Local Testing (Tunneling)

* Tool: Ngrok ^5.0.0-beta.2 (npm wrapper)

# Build Environment

* System: Gradle 8.13.0 (Version Catalog)
```

## 3. 🧪 기능 목록

### 3.1. ⚡ 사용자의 상호작용에 따른 앱 -> 웹으로의 즉각적인 동기화
- 안드로이드 앱에서의 변경 사항을 웹에 즉각 반영합니다.
	- 단 컴포넌트 끌기, 확대 등의 액션은 사용자가 손을 뗀 후에 반영됩니다. 
	
### 3.2. 🞧 웹 컴포넌트 생성
- 안드로이드 애플리케이션의 사이드바에서 컴포넌트를 생성할 수 있습니다.
- 생성할 수 있는 컴포넌트는 다음과 같습니다.
  - 텍스트
  - 버튼
  - 이미지
  - 드롭다운
  - 입력 상자
  > TODO: 구현 후 컴포넌트 추가하기
  
### 3.3. 🖌️ 웹 컴포넌트 커스텀 기능
- 앱 캔버스 내의 컴포넌트에 다음과 같은 스타일을 적용할 수 있습니다.
		- 위치 (offSetX, offSetY)
		- 크기 (height, width)
    - 글씨 색상 (fontColor)
    - 글씨 크기 (fontSize)
    - 글씨 두께 (fontWeight)
    - 글꼴 (fontFamily)
    - 배경 색상 (backgroundColor)
    - 경계선 굴곡 (borderRadius)
    - 경계선 색상 (borderColor)
- 버튼 컴포넌트와 이벤트를 매핑할 수 있습니다. 
		- `trigger` 발생 시 `target`에게 `action` 수행, 사용할 값은 `value` 
		ex> `buttonClick` 발생시 `componentId: abc123` 에게 `SET_TEXT` 수행, 사용할 값은 `5`
		
### 3.4. 🖱️ 컴포넌트 선택, 드래그 기능
- 사용자가 컴포넌트의 위치와 크기를 손쉽게 조정할 수 있습니다. 
- 컴포넌트를 1회 클릭 시 컴포넌트가 선택됩니다. 이 상태에서 사용자는 사이드바에서 
선택된 컴포넌트에 2.3의 스타일 적용을 수행할 수 있습니다.
- 컴포넌트를 2회 클릭 시 컴포넌트의 8방향에서 핸들이 등장합니다. 이 상태에서 사용자는 핸들을 끌어 
선택된 컴포넌트의 크기를 조절할 수 있습니다. 

### 3.5. 🧱 웹 컴포넌트의 Layer Level 설정 가능
- 사용자는 모든 컴포넌트 리스트를 확인할 수 있습니다. 
- 컴포넌트는 Layer 값을 가질 수 있으며, Layer값이 높을 수록 더 우선적으로 표시됩니다(Layer 낮은 컴포넌트를 가림)

### 3.6 📁 멀티 캔버스 기능
- 사용자는 메인 화면에서 캔버스

### 3.6. 🔗 사용자가 만든 웹페이지 링크 제공
- 사용자가 안드로이드 앱에서 구현한 캔버스를 웹 페이지로 배포하여 사용자에게 제공합니다.
- 사용자는 우상단의 버튼을 눌러 자신이 만든 웹 페이지를 HTML 파일 형태로 다운 받을 수 있습니다. 
> TODO: 기능 구현 후 계속 추가하기


## 4. ▶️🔧 실행 방법

[실행 방법](https://www.notion.so/2b59e4bfb6548077a3bbcc5c552b67aa?source=copy_link)
