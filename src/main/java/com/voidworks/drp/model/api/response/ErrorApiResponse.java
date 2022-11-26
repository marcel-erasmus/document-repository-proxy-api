package com.voidworks.drp.model.api.response;

import com.voidworks.drp.model.api.error.Error;
import com.voidworks.drp.model.api.error.ValidationError;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ErrorApiResponse {

    private List<Error> errors;
    private List<ValidationError> validationErrors;

}
