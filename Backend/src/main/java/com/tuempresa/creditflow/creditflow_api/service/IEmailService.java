package com.tuempresa.creditflow.creditflow_api.service;

import java.io.IOException;

public interface IEmailService {
    /**
     * Envía un correo electrónico a un destinatario específico.
     *
     * @param to      La dirección de correo electrónico del destinatario.
     * @param subject El asunto del correo electrónico.
     * @param body    El cuerpo del correo electrónico, generalmente en formato de texto.
     */
    void sendEmail(String to, String subject, String body) throws IOException;
}
