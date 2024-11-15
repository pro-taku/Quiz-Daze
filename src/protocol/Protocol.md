# 퀴즈다제~ protocol

---

> ### 들어가기 앞서
> 이 문서는 해당 서비스의 프로토콜에 대한 내용이 담겨있습니다.

### 목차
1. structure
2. Header
3. Body

---

# structure

해당 서비스에선 Custom Protocol을 이용해 통신합니다.
Protocol은 크게 Header와 Body로 구성되어 있고, 이 둘은 `&`로 구분하고 그 내용을 `{ }` 안에 표시합니다.

```
{Header}&{Body}
```

또한 내용을 구성함에 있어서 줄바꿈(`\n`)으로 이를 구분하고,
각각의 내용은 key-value 형식으로 나타냅니다.
key-value는 `:`로 구분합니다.

이때,
- header의 key와 value는 모두 `string` 타입입니다.
- body의 key는 `string` 타입이고 value는 `json` 형식으로 나타냅니다.

```
{
    sender:c
    type:GET\n
    url:/example/load\n
    status:200\n
}&{
    title:{
        "k1":"v1",
        "k2":"v2",
        "k3":"v3"
    }\n
    content:{
        "k1":"v1",
        "k2":"v2", 
        "k3":"v3"
    }\n
    author:{
        "k1":"v1",
        "k2":"v2",
        "k3":"v3"
    }
}
```

## Header

Header는 다음과 같은 정보가 담겨 있습니다.
- `sender`: 요청을 보내는 측
- `type`: 요청의 처리 방법
- `url`: 요청의 주소

### sender

sender는 요청을 보내는 측을 나타내며, Client와 Server를 구분합니다.

> `c`: Client  
> `s`: Server

### type

type은 요청을 처리하는 방식을 나타냅니다.

> `GET`: 데이터를 요청  
> `POST`: 데이터를 전송  
> `UPDATE`: 데이터를 수정  
> `DELETE`: 데이터를 삭제

### url

url은 요청의 주소를 나타냅니다. 문자열로 이루어져있으며, `/`로 시작하며 `/`로 API Level을 구분합니다

```
url:"/api/example/use"
```

### status

status는 요청의 처리 결과를 나타내며, `OK`와 `ERROR`로 구분합니다.

## Body

Body는 요청을 처리함에 있어서 필요한 데이터를 전달하거나, 또는 요청의 처리 결과를 전달할 수 있습니다.
또한, `json` 형식을 따르며 `string` 타입의 key와 `object` 타입의 value를 가지게 됩니다.