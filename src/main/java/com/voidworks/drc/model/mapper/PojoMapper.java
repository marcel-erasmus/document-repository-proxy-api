package com.voidworks.drc.model.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PojoMapper {

    public <T> T map(Object source, T destination) {
        BeanUtils.copyProperties(source, destination);

        return destination;
    }

}
