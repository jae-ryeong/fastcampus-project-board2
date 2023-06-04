package com.fastcampus.projectboard2.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages){
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH/2), 0);  // 0이랑 비교해서 더 큰수를 쓰겠다 ( 첫 페이지에 음수가 들어가지 않게 해준다.)
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);

        return IntStream.range(startNumber, endNumber).boxed().toList();
    }

    public int currentBarLength() { // 페이징네이션 바 길이
        return BAR_LENGTH;
    }
}
