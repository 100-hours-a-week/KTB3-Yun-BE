package KTB3.yun.Joongul.common.dto;

import lombok.Getter;

@Getter
public class ApiResponseDto<T> {
    private final String message;
    private final T data;

    public ApiResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
