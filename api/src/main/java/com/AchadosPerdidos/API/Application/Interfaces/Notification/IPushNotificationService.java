package com.AchadosPerdidos.API.Application.Interfaces.Notification;

import java.util.List;
import java.util.Map;

public interface IPushNotificationService {

    boolean sendToDevice(String deviceToken, String title, String message, Map<String, String> data);

    boolean sendToMultipleDevices(List<String> deviceTokens, String title, String message, Map<String, String> data);

    boolean isAvailable();
}
