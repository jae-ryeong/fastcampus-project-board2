package com.fastcampus.projectboard2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("비즈니스 로직 - 페이지네이션")
@SpringBootTest
class PaginationServiceTest {

    private final PaginationService sut;

    public PaginationServiceTest(@Autowired PaginationService sut) {
        this.sut = sut;
    }

    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면, 페이징 바 리스트를 만들어준다.")
    @MethodSource
    @ParameterizedTest  // 동일한 메서드를 여러번 출력하는 어노테이션
    public void givenPageNumber(int currentPageNumber, int totalPages, List<Integer> expected) throws Exception{
        //given


        //when
        List<Integer> actual = sut.getPaginationBarNumbers(currentPageNumber, totalPages);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenPageNumber() {    // @MethodSource 어노테이션으로 매개변수를 전해줄때 사용, 메서드 이름을 똑같이 해야한다.
        return Stream.of(
                Arguments.arguments(1, 13, List.of(0,1,2,3,4)), // 페이지번호는 0부터 시작하기 때문에 0부터 시작 후 view에서 +1을 해준다.
        )
    }
}