# JAVA / SPRING Test 를 작성해보자

## 테스트란 무엇인가?

### 테스트란?

함수의 결가과값 과 기댓갑을 비교하여 전체적으로 돌리지 않더라고 한 기능에 집중해서 개발 가능하게 해주는 방법  
회귀버그 방지 - 좋은 설계

### 개발자의 고민

1. 무의미한 테스트 (Recap)  
   findById 와 같은 test 는 과연 필요할 것인가?  
   -> springboot 와 h2 db 까지 띄우며 해야할 테스트일까??  
   -> 모든 메소드의 test X
2. 느리고 쉽게 깨지는 테스트
3. 테스트가 불가한 코드  
   ex)현재 시간을 바로 찍는 테스트?

## 테스트의 필요성, 테스트 3분리

### 필요성

테스트는 왜 필요한가?  
-> 레거시 코드 **_레거시 코드란, 단순히 테스트 루틴이 없는코드_**  
-> Regression _잘돌아가던 코드가 이번 배코포로 인해 동작하지 않는 상황_

> 2005년 구글에서도 생산성이 떨어지는 일이 있었는데 이유는 테스트 없이 진행 으로 인해 분안에 떨며 릴리즈를 진행  
> `구글엔지니어는 이렇게 일한다`

-> 좋은 아키텍처를 유도 **_SOLID_**  
단일 책임 원칙  
개방 폐쇄 원칙 (확자에는 열려있어야하고 수정에는 닫혀 있어야 한다)  
리스코프 치환 원칙(슈퍼 클래스의 계약을 서브클래스가 제대로 치환하는지 확인)  
인터페이스 분리 원칙 (불필요한 의존선을 제거)
**의존성 역전 원칙**

### 테스트의 3분류

API 테스트 , 통합 테스트, 단위 테스트 -> 사람마다 모호 하고 애매함 별로 안좋은 말인듯?

> large test, medium test, small test `구글엔지니어는 이렇게 일한다`

- small test 80%  
  단일 서버, 단일 프로세스, 단일 스레드, 디스크 I/O X, Blocking call X
- medium test 15%  
  단일 서버, 멀티 프로세스, 멀티 스레드 -> h2 등 test db O
  -> 대부분의 spring 개발자는 중형 테스트를 너무 많이 한다..
- large test 5%
  멀티 서버, End to end test

우리가 집중 해야 하는건 바로 small test 를 늘릴 수 있는 환경을 만들구 리팩토링 해야하지 않을까??

## 테스트에 필요한 개념

### 개념

SUT : System under test (테스트 하려는 대상) -> assert 에 들어가는 대상  
BDD : Behaviour driven development (given - when - then) or (arrannge - act - assert)  
상호작용 테스터 : 대상 함수의 구현을 호출하지 않으면서 그 함수가 어떻게 호출되는지를 검증하느 기법  
(메소드가 실제 호출 되는지 검증하는 기법) -> 구현에 집중하는거라 조금 애매  
상태 검증 vs 행위 검증 : 상태(어떤 값을 시스템에 넣을때 나오는 결과값을 기댓값과 비교) 행위 (상호작용테스트)  
테스트 픽스처 : 테스트에 필요한 작원을 생성하는것
비욘세 규칙 : 유지하고 싶은 상태나 정책이 있으면 알아서 만들어야한다

테스트는 정책이고 계악 이다!!!!! -> 프로그램이 지켜야할 계약 캍은것

### 대역

가짜객체를 두고 대역이란 말을 쓴다 만약 회원 가입때마다 이메일을 날려야 하는 부부은 테스트를 할 필요가 없지만 무조건 들어가야할 값이라면 가짜 객체를 만들어 작성

Dummy : 일을 시켜도 아무일도 하지않는 객체
Fake : Local 에서 사용하거나 테스트에서 사용하기위해 만들어진 까자, 자체적인 로직이 따로 있다는게 특징
Stub : 외부 연동 컴포넌트들에 사용을 많이하며 미리 준비된 값을 출력하는 객체 -> 모키토 를 주로 이요
Mock : 메서드 호출을 확인하기 위한 객체
Spy : 메소드 호출을 전부 기록했다가 나중에 확인하기 위한 객체

## 의존성과 Testability 1

### DIP

의존성 역전과 의존성 주입은 완전 다른 개념이다.
의존성 역전이란?

1. 상위 모듈은 하위 모듈에 의존해서는 안되다. 상위 모둘과 하위 모듈 모드 추상화에 의존해야 한다.
2. 추상화는 세부 사항에 의존해서는 안된다. 세부사항이 추상화에 의존해야 한다.

### 의존성과 테스트

테스트를 잘하려면 의존성 주입과 의존성 역전을 잘 다룰 수 있어야 합니다.

의존성이 숨겨져 있으면 테스트 시 좋지 않다 -> 결과 를 비교 해야 하기 때문에  
우리가 외부 라이브러리의 도움으로 테스트를 성공 시킬 수 있다면 테스트가 보내는 신호 -> 해결 해야함  
-> **우린 의존성 역전을 사용 해봐야한다.**  
테스트 해야하는 부분에 의존선을 interface로 만들어 두면 대역 객체들을 이용하여 테스트가 가능하다

### Testability

얼마나 쉽게 input을 변경하고, output을 쉽게 검증할 수 있는가?
