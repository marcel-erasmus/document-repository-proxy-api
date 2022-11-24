package com.voidworks.drc.model.api.response;

import com.voidworks.drc.model.api.error.Error;
import com.voidworks.drc.model.api.error.ValidationError;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BaseApiResponse {

    private List<Error> errors;
    private List<ValidationError> validationErrors;

}
