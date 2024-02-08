package ru.practicum.ewm.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Util {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public interface Marker {
        interface onCreate {}
        interface onUpdate {}
    }

    public PageRequest getPageRequestAsc(String sortBy, Integer from, Integer size) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(sortBy).ascending());
    }

    public PageRequest getPageRequestDesc(String sortBy, Integer from, Integer size) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(sortBy).descending());
    }
}
