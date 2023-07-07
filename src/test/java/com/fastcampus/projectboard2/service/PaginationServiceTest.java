package com.fastcampus.projectboard2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("비즈니스 로직 - 페이지네이션")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
class PaginationServiceTest {

    private final PaginationService sut;

    public PaginationServiceTest(@Autowired PaginationService sut) {
        this.sut = sut;
    }

    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면, 페이징 바 리스트를 만들어준다.")
    @MethodSource
    @ParameterizedTest(name = "[{index}] {0}, {1} => {2}")  // 동일한 메서드를 여러번 출력하는 어노테이션, index는 현재 테스트 번호, 입력 값 => 출력 값 (실행해보면 안다)
    public void givenPageNumber(int currentPageNumber, int totalPages, List<Integer> expected) throws Exception{
        //given
        //when
        List<Integer> actual = sut.getPaginationBarNumbers(currentPageNumber, totalPages);  // currentPageNumber도 index 넘버이기 때문에 0부터 출발한다.

        //then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenPageNumber() {    // @MethodSource 어노테이션으로 매개변수를 전해줄때 사용, 메서드 이름을 똑같이 해야한다.
        return Stream.of(
                Arguments.arguments(0, 13, List.of(0,1,2,3,4)), // 페이지번호는 0부터 시작하기 때문에 0부터 시작 후 view(홈페이지에 보이는 페이지 번호)에서 +1을 해준다.
                Arguments.arguments(1, 13, List.of(0,1,2,3,4)),
                Arguments.arguments(2, 13, List.of(0,1,2,3,4)),
                Arguments.arguments(3, 13, List.of(1,2,3,4,5)),
                Arguments.arguments(4, 13, List.of(2,3,4,5,6)),
                Arguments.arguments(5, 13, List.of(3,4,5,6,7)),
                Arguments.arguments(6, 13, List.of(4,5,6,7,8)),
                Arguments.arguments(10, 13, List.of(8,9,10,11,12)),
                Arguments.arguments(11, 13, List.of(9,10,11,12)),   // 12가 마지막이므로 바가 짧아진다.
                Arguments.arguments(12, 13, List.of(10,11,12))    // index는 0부터 시작하기 때문에 페이지 번호는 0부터 12페이지까지 있다.
        );
    }

    @DisplayName("현재 설정되어 있는 페이지네이션 바의 길이를 알려준다.")
    @Test
    public void CurrentBarLength() throws Exception{
        //when
        int barLength = sut.currentBarLength();

        //then
        assertThat(barLength).isEqualTo(5);
    }
}