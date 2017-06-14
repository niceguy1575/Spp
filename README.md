# Spp Project : TCP & UDP Protocol

### Welcome!

    This Project is for software development project group 1 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 이 프로젝트는 TCP와 UDP를 이용한 파일 전송 프로토콜을 기반으로 자료를 분석하는 통계 서버를 구축하는것을 목표로 진행되었습니다.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Release ver 0.2 ~ 0.4 까지의 내용을 확인하시고 싶으시면 release내용을 확인해주시길 바랍니다.

><li>v0.2 : file transfer with tcp / udp & check integrity</li>
 <li>v0.3 : move Directory / transfer speed</li>
 <li>v0.35 : Issue 1-3 solved version. (Bug fix) </li>
 <li> v0.4 : 이어받기 및 파일 용량에 따른 프로토콜 선택</li>

## 사용 방법

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;v0.5를 사용하기 위한 방법을 말씀드립니다. 잘못된 사용방법은 compile error를 유발할 수 있습니다.

><li> 본 코드는 Eclipse IDE환경에서 동작합니다. command 명령을 통한 실행이 불가능합니다. </li>
<li> Local에서 Test하실 경우 R.jar에 있는 파일을 다운받아 Eclipse에 Build Path해주시기 바랍니다. </li>
<li> 서버파일은 `
Server : TCP & UDP
` 두가지로 구성됩니다. <u> 반드시 UDP Server 먼저 동작시켜주시기 바랍니다. </u> </li>
<li> Server파일은 R Server가 동작하고 있는 상태에서만 실행됩니다. (아래 설명 별첨) </li>
<li> Server를 먼저 동작시킨 후에 Client를 동작해주시기 바랍니다. </li>

### R 설치
[https://www.r-project.org/](url) 에 들어가 R을 설치해주시기 바랍니다.

또한 R 서버의 동작을 위해 설치 후 R에서 다음을 실행시켜주세요.

스크립트를 콘솔에 작성하고 Enter를 누루시면 됩니다.  

```{r }
install.packages("Rserve")
library(Rserve)
Rserve()
```


## Input & Output

### Input 조건
<li> txt (Delimiter : " " ) or csv (Delimiter : ",") </li>
<li> 첫번째라인은 header(변수 명) 두번째 라인부터 자료(anything which is atomic) </li>

example of data

| header1       | header2       | header3  |
| ------------- |:-------------:| --------:|
| observe[1,1]  | observe[1,2]  | observe[1,3] |
| observe[2,1]  | observe[2,2]  | observe[2,3] |
| observe[3,1]  | observe[3,2]  | observe[3,3] |

### Output

1. 전송한 파일
2. 자료 탐색 ( 1,3 사분위수, 중앙값, 평균 등 )
3. 회귀분석 결과 (회귀 계수 및 절편)
4. 자료 탐색결과 시각화 : Boxplot || Histogram
5. (만약 독립변수가 1개일경우) 회귀분석 Plot 
  

## 개발 일지
#### 2017-04-05 : basic UDP (Server)
UDP communication updated

#### 2017-04-10 : basic TCP && TCP file transfer / make TCP Branch (Server)
1. TCp communication updated
2. TCP File Transfer

#### 2017-04-21 :  measure transfer speed (Client)
[Client Branch] - Transfer Speed Check / Integrity

#### 2017-04-23 : transfer directory (Server)
[Server Branch] - Transfer Directory

#### 2017-04-24 : Merge client with Server (Merge conflict solved.) (Client)
Merge all

#### 2017-05-01 : Issue 1-3 solved. (Server)
Bug fixed version

#### 2017-05-12 : Connect with Trello. (Server & Client)
make connection with Trello.

#### 2017-05-24 : Connect Java with R (Server)
make connection with R

#### 2017-05-25 : Construct R Server & ClientUI update (Server)
we can do statistical analysis through JAVA!
Also, client bug fixed.

#### 2017-05-26 : Construct R Server & ClientUI update (Server)
JAVA의 통계적 계산의 용이함을 위해 R과 JAVA를 연결함.

#### 2017-05-27 : R 연결 및 기능을 활용 (Server)
R을 이용하여 회귀분석 / 서머리 기능을 추가하였음. 

#### 2017-05-29 : v0,4 Bug fix1 (Client)
파일 이어받기 및 연속전송 오류 수정

#### 2017-05-30 : 파일 연속전송 bug fix (Client)

파일이 여러개의 전송이 되지 않음을 확인하였고, Client UI에서의 문제로 확인

Server와 협의하여 파일 전송 문제 논의

#### 2017-06-03 : 시각화 기능 추가 (Server)
Boxplot, Histogram 추가

#### 2017-06-04 : v0,4 Bug fix2 (Client)
Null Pointer Exception 제거

#### 2017-06-04 : JAVA Path scanner error (Server & Client)
JAVA Scanner Error 수정

#### 2017-06-14 : Fianl bug fix before release (Server & Client)
1. 파일 연속전송 에러 수정
2. 서버 자원 할당문제 해결 (잠정적임)