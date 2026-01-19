package com.example.burnchuck.common.annotation;

import com.example.burnchuck.common.enums.NotificationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateNotification {
    NotificationType notificationType();
}
