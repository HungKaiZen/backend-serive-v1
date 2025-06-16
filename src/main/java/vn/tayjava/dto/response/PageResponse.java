package vn.tayjava.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private T items;
}
