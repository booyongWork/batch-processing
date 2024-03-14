package com.example.batchprocessing;
//NOTE. record 클래스는 데이터를 저장하기 위해 사용되며, 그 외에 메서드를 추가하거나 상속을 받지 않음
// 이러한 특성으로 인해 record 클래스는 간단하게 데이터를 표현하고 다루는 데 유용.
// 코드를 간결하게 만들어주고 데이터의 불변성을 보장함으로써 코드의 안정성을 높이는 데 도움이 됨
public record Person(String firstName, String lastName, String gender, boolean married, int age) {
}
