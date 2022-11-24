package com.voidworks.drc.model.api.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ApiResponse<T> {

    private List<T> data;

}
