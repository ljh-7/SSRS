package com.huawei.ibooking.validator;

import com.huawei.ibooking.model.StudyRoomDO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StudyRoomTimeValidator implements ConstraintValidator<ValidTimeRange, StudyRoomDO> {

    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(StudyRoomDO studyRoomDO, ConstraintValidatorContext constraintValidatorContext) {
        Integer startTime = studyRoomDO.getStartTime();
        Integer endTime = studyRoomDO.getEndTime();
        return startTime < endTime;
    }
}
