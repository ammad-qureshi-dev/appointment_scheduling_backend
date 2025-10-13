/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.CommsRequest;
import com.booker_app.backend_service.controllers.response.*;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.enums.ContactMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseSeverity.ERROR;
import static com.booker_app.backend_service.controllers.response.ResponseType.EMAIL_NOT_SENT;

@Slf4j
@Service
public class CommunicationService {

	private final JavaMailSender emailSender;
	private final SimpleMailMessage simpleMailMessage;
	private final ServiceResponse<?> serviceResponse;

	public CommunicationService(JavaMailSender emailSender, SimpleMailMessage simpleMailMessage,
			ServiceResponse<?> serviceResponse) {
		this.emailSender = emailSender;
		this.simpleMailMessage = simpleMailMessage;
		this.serviceResponse = serviceResponse;
	}

	public CommsResponse sendCommunication(ContactMethod commsType, CommsRequest<?> commsRequest) {
		switch (commsType) {
			case EMAIL -> {
				return sendCommunicationViaEmail(commsRequest);
			}
			case PHONE -> throw new ServiceResponseException(ResponseType.NOT_IMPLEMENTED_YET);
			default -> throw new RuntimeException("Method does not exist");
		}
	}

	private CommsResponse sendCommunicationViaEmail(CommsRequest<?> commsRequest) {
		if (!Objects.isNull(commsRequest.getSendAt())) {

			// ToDo: Add it to cron job scheduler

			return CommsResponse.builder().recipient(commsRequest.getRecipient()).sentStatus(false)
					.sentAt(commsRequest.getSendAt()).responseId(UUID.randomUUID()).build();
		} else {
			commsRequest.setSendAt(LocalDate.now());
		}

		simpleMailMessage.setTo(commsRequest.getRecipient());
		simpleMailMessage.setSubject(commsRequest.getSubject());
		simpleMailMessage.setText(commsRequest.getMessageContent());

		var sentStatus = false;

		try {
			emailSender.send(simpleMailMessage);
			sentStatus = true;
		} catch (MailException e) {
			log.error(e.getMessage());
			serviceResponse.getAlerts()
					.add(ResponseData.builder().responseSeverity(ERROR).responseType(EMAIL_NOT_SENT).build());
		}

		return CommsResponse.builder().recipient(commsRequest.getRecipient()).sentStatus(sentStatus)
				.sentAt(commsRequest.getSendAt()).responseId(UUID.randomUUID()).subject(commsRequest.getSubject())
				.build();
	}
}
