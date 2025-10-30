package com.tuempresa.creditflow.creditflow_api.configs;

import com.tuempresa.creditflow.creditflow_api.enums.*;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUserCreator implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final KycVerificationRepository kycVerificationRepository;
    private final CreditApplicationRepository creditApplicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreditApplicationHistoryRepository creditApplicationHistoryRepository;

    // Constantes de prueba
    private static final String DEFAULT_PASS = "Pass1234!";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("--- 💾 Iniciando precarga de datos (Seeding)... ---");

            // 1. Crear y guardar Usuarios
            List<User> operators = seedOperators();
            List<User> pymeUsers = seedPymeUsers();

            // Combinar todos los usuarios para referencia futura (17 usuarios)
            List<User> allUsers = new ArrayList<>();
            allUsers.addAll(operators);
            allUsers.addAll(pymeUsers);

            // 2. Crear Compañías (asociadas a los primeros 12 PYME)
            List<Company> companies = seedCompanies(pymeUsers.subList(0, 12));

            // 3. Crear Verificaciones KYC (usando TODA la lista de usuarios, ya que el método lo maneja)
            seedKycVerifications(allUsers, companies);

            // 4. Crear Solicitudes de Crédito
            List<CreditApplication> applications = seedCreditApplications(companies.subList(0, 7)); // <-- Capturar las Apps

            // 5. CREAR HISTORIAL (30 Registros)
            seedApplicationHistory(applications, operators); // <-- NUEVA LLAMADA

            log.info("--- ✅ Precarga de datos finalizada. ---");
        } else {
            log.info("--- ⏭️  Base de datos ya poblada. Omitiendo Seeding. ---");
        }
    }

    // Dentro de la clase DefaultUserCreator

    private void seedApplicationHistory(List<CreditApplication> applications, List<User> operators) {
        log.info("💾 Creando 30 registros de historial para las solicitudes...");
        List<CreditApplicationHistory> historyList = new ArrayList<>();

        // Identificando Operadores para el historial manual
        User opSofia = operators.get(0); // Operador 1
        User opMarcelo = operators.get(1); // Operador 2

        // Referencias a las aplicaciones para facilitar la creación de eventos
        CreditApplication app1 = applications.get(0); // APPROVED, Company 1
        CreditApplication app2 = applications.get(1); // REJECTED, Company 2
        CreditApplication app3 = applications.get(2); // UNDER_REVIEW, Company 3
        CreditApplication app4 = applications.get(3); // PENDING, Company 4 (sin docs, necesita más acción)
        CreditApplication app6 = applications.get(5); // PENDING, Company 6 (KYC incompleto)

        LocalDateTime now = LocalDateTime.now().minusHours(10); // Base de tiempo

        // ====================================================================
        // BLOQUE 1: Creación y Primera Revisión (15 Registros)
        // ====================================================================

        // 1. REGISTROS DE CREACIÓN (Base para las 7 Apps)
        for (int i = 0; i < applications.size(); i++) {
            CreditApplication app = applications.get(i);
            historyList.add(CreditApplicationHistory.builder()
                    .creditApplication(app)
                    .actionType(CreditApplicationActionType.CREATION)
                    .action("Solicitud inicial creada por el cliente.")
                    .operator(app.getCompany().getUser()) // El cliente PYME es el "operador" de la creación
                    .createdAt(now.plusMinutes(i * 5L))
                    .build());
        }

        // 2. Transición de PENDING a UNDER_REVIEW (App 3, 6, 7)
        // App 3: Pasa a revisión rápidamente
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app3)
                .actionType(CreditApplicationActionType.STATUS_CHANGE)
                .action("Estado cambiado de PENDING a UNDER_REVIEW")
                .comments("Inicio de análisis financiero y validación de documentos.")
                .operator(opSofia)
                .createdAt(now.plusHours(1))
                .build());

        // App 7: Pasa a revisión, pero con KYC pendiente
        CreditApplication app7 = applications.get(6);
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app7)
                .actionType(CreditApplicationActionType.STATUS_CHANGE)
                .action("Estado cambiado de PENDING a UNDER_REVIEW")
                .comments("Revisión iniciada. Solicitud retenida por falta de Acta de Representación de la empresa.")
                .operator(opMarcelo)
                .createdAt(now.plusHours(1).plusMinutes(30))
                .build());

        // 3. Comentario/Acción sobre Solicitudes con Score Bajo/Alto
        // App 1: Comentario de aprobación preliminar (Alto Score)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app1)
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Comentario de Analista: Solicitud con alto potencial. Priorizar.")
                .comments("Score inicial de 95/100. Pasar a firma electrónica.")
                .operator(opSofia)
                .createdAt(now.plusHours(2))
                .build());

        // App 2: Comentario de bajo score (previo al rechazo)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app2)
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Comentario de Analista: Score bajo (15). Contactar por documentación faltante.")
                .comments("Riesgo elevado. Analizar posibilidad de rechazo inmediato.")
                .operator(opMarcelo)
                .createdAt(now.plusHours(2).plusMinutes(15))
                .build());

        // App 5: Comentario sobre el límite de riesgo
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(applications.get(4))
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Comentario de Sistema: Límite de exposición sectorial superado.")
                .comments("El monto de $400k excede el máximo permitido para el sector actual de la empresa. Pasa a rechazo.")
                .operator(opSofia)
                .createdAt(now.plusHours(2).plusMinutes(30))
                .build());

        // 4. Rechazo Automático/Decisión (App 2, App 5)

        // App 5: Rechazo definitivo por límite de riesgo (Score 50, pero muy alto monto)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(applications.get(4))
                .actionType(CreditApplicationActionType.STATUS_CHANGE)
                .action("Estado cambiado de PENDING a REJECTED")
                .comments("Rechazado por exceder el límite de exposición crediticia sectorial.")
                .operator(opSofia)
                .createdAt(now.plusHours(3))
                .build());

        // App 2: Rechazo definitivo por riesgo bajo
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app2)
                .actionType(CreditApplicationActionType.STATUS_CHANGE)
                .action("Estado cambiado de PENDING a REJECTED")
                .comments("Rechazado por score de riesgo insuficiente (15) y poca solvencia demostrada en extractos.")
                .operator(opMarcelo)
                .createdAt(now.plusHours(3).plusMinutes(10))
                .build());


        // ====================================================================
        // BLOQUE 2: Aprobaciones, Actualizaciones y Trazabilidad (15 Registros)
        // ====================================================================

        // 5. Aprobación (App 1)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app1)
                .actionType(CreditApplicationActionType.STATUS_CHANGE)
                .action("Estado cambiado de PENDING a APPROVED")
                .comments("Aprobación finalizada. Solicitud lista para desembolso.")
                .operator(opSofia)
                .createdAt(now.plusHours(4))
                .build());

        // 6. Actualización por el cliente (App 4)
        // El cliente sube un documento adicional (simulación de Update)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app4)
                .actionType(CreditApplicationActionType.UPDATE)
                .action("Cliente actualiza información: sube Certificado de PyME.")
                .comments("Cliente sube documento: Certificado PyME.")
                .operator(app4.getCompany().getUser())
                .createdAt(now.plusHours(5))
                .build());

        // App 4: Pasa a revisión tras la actualización
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app4)
                .actionType(CreditApplicationActionType.STATUS_CHANGE)
                .action("Estado cambiado de PENDING a UNDER_REVIEW")
                .comments("Revisión iniciada tras actualización de documentos por cliente.")
                .operator(opMarcelo)
                .createdAt(now.plusHours(5).plusMinutes(30))
                .build());

        // 7. Acciones de Analista en App 3 (En Revisión)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app3)
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Analista solicitó aclaración sobre flujos de caja.")
                .comments("Se envió email al cliente (luis.cruz@pyme3.com) solicitando detalle de egresos no recurrentes.")
                .operator(opSofia)
                .createdAt(now.plusHours(6))
                .build());

        // 8. Acciones sobre App 6 (PENDING por KYC)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app6)
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Analista verifica estado de KYC de Compañía.")
                .comments("Se notificó al equipo de KYC que la solicitud está retenida a la espera del Balance Anual de la Compañía 6.")
                .operator(opMarcelo)
                .createdAt(now.plusHours(7))
                .build());

        // 9. Simulación de Cambio de Comentario (App 7)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app7)
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Comentario de Analista: Se reintentó contactar al representante legal.")
                .comments("Se dejó mensaje de voz al contacto +5491145007777 para pedir acta pendiente.")
                .operator(opSofia)
                .createdAt(now.plusHours(8))
                .build());

        // 10. Movimientos internos de revisión (App 4, 3, 7) - 6 entradas más para llegar a 30

        // App 4 (revisión de nuevos docs)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app4)
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Documento adicional (Certificado PyME) validado.")
                .comments("El nuevo certificado incrementa el score potencial, continuar con revisión crediticia.")
                .operator(opMarcelo)
                .createdAt(now.plusHours(8).plusMinutes(30))
                .build());

        // App 3 (revisión avanzada)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app3)
                .actionType(CreditApplicationActionType.COMMENT)
                .action("Pasa a revisión de Gerencia.")
                .comments("Revisión de crédito finalizada, se espera aprobación de riesgo de Gerencia.")
                .operator(opSofia)
                .createdAt(now.plusHours(9))
                .build());

        // App 7 (aún en UNDER_REVIEW)
        historyList.add(CreditApplicationHistory.builder()
                .creditApplication(app7)
                .actionType(CreditApplicationActionType.UPDATE)
                .action("Sistema actualiza el recordatorio de documentación pendiente.")
                .comments("Recordatorio automático enviado al cliente por email.")
                .operator(opSofia) // Simula acción automatizada asociada a un operador
                .createdAt(now.plusHours(9).plusMinutes(15))
                .build());

        // 4 registros adicionales (App 6, 4, 3, 7)
        historyList.add(CreditApplicationHistory.builder().creditApplication(app6).actionType(CreditApplicationActionType.COMMENT).action("Revisión de antigüedad de CUIT.").operator(opMarcelo).createdAt(now.plusHours(9).plusMinutes(20)).build());
        historyList.add(CreditApplicationHistory.builder().creditApplication(app4).actionType(CreditApplicationActionType.COMMENT).action("Pendiente de asignación a analista Senior.").operator(opSofia).createdAt(now.plusHours(9).plusMinutes(25)).build());
        historyList.add(CreditApplicationHistory.builder().creditApplication(app3).actionType(CreditApplicationActionType.UPDATE).action("Gerencia da pre-aprobación, esperando firma.").operator(opSofia).createdAt(now.plusHours(9).plusMinutes(30)).build());
        historyList.add(CreditApplicationHistory.builder().creditApplication(app7).actionType(CreditApplicationActionType.COMMENT).action("Analista llamó a contacto alternativo.").operator(opMarcelo).createdAt(now.plusHours(9).plusMinutes(35)).build());


        // 11. Persistir todo el historial
        creditApplicationHistoryRepository.saveAll(historyList);
        log.info("✅ 30 registros de historial creados y persistidos.");
    }

    private List<CreditApplication> seedCreditApplications(List<Company> companies) {
        log.info("💾 Creando 7 Solicitudes de Crédito y asociando documentos de riesgo...");

        // NOTA: Se creará la aplicación, se le agregarán los documentos, se calculará
        // el score y luego se guardarán todas las entidades en cascada.

        // ------------------------------------------------
        // Solicitud 1: APPROVED (Company 1) - Score Alto
        // ------------------------------------------------
        CreditApplication app1 = CreditApplication.builder()
                .company(companies.getFirst()).amount(new BigDecimal("250000.00")).termMonths(36)
                .status(CreditStatus.APPROVED).creditPurpose(CreditPurpose.INVERSION)
                .operatorComments("Aprobación automática. Score superior y KYC completo.")
                // No asignamos riskScore aquí; lo calcularemos después.
                .build();

        createRiskDocument(app1, "Plan de inversión", 40, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246452/Plan_de_inversi%C3%B3n_2_bu8auk.pdf");
        createRiskDocument(app1, "Flujo de Caja Proyectado", 35, "url/docs/https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246455/FLUJO_DE_CAJA_PROYECTADO_3_qcrk5v.pdf");
        createRiskDocument(app1, "Poderes del representante legal", 20, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246452/Plan_de_inversi%C3%B3n_2_bu8auk.pdf");
        // Score esperado: 40 + 35 + 20 = 95
        app1.calculateRiskScore();

        // ------------------------------------------------
        // Solicitud 2: REJECTED (Company 2) - Score Bajo
        // ------------------------------------------------
        CreditApplication app2 = CreditApplication.builder()
                .company(companies.get(1)).amount(new BigDecimal("30000.00")).termMonths(6)
                .status(CreditStatus.REJECTED).creditPurpose(CreditPurpose.COMPRA_INVENTARIO)
                .operatorComments("Rechazo manual. Score de riesgo bajo post-análisis financiero.")
                .build();

        createRiskDocument(app2, "Extracto bancario", 10, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246450/Extracto_Bancario_2_owfyl6.pdf");
        createRiskDocument(app2, "Estatuto social", 5, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246451/ESTATUTO_SOCIAL_2_n84agv.pdf");
        // Score esperado: 10 + 5 = 15
        app2.calculateRiskScore();


        // ------------------------------------------------
        // Solicitud 3: UNDER_REVIEW (Company 3) - Score Medio
        // ------------------------------------------------
        CreditApplication app3 = CreditApplication.builder()
                .company(companies.get(2)).amount(new BigDecimal("90000.00")).termMonths(18)
                .status(CreditStatus.UNDER_REVIEW).creditPurpose(CreditPurpose.MEJORA_INFRAESTRUCTURA)
                .operatorComments("Pendiente de la firma de contrato digital.")
                .build();

        createRiskDocument(app3, "Estados de Cuenta Bancarios", 25, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246451/Extracto_Bancario_1_wbjau9.pdf");
        createRiskDocument(app3, "Acta constitutiva", 35, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246453/ACTA_CONSTITUTIVA_Y_ESTATUTO_SOCIAL_1_yajoar.pdf");
        // Score esperado: 25 + 35 = 60
        app3.calculateRiskScore();


        // ------------------------------------------------
        // Solicitud 4: PENDING (Company 4) - SIN DOCUMENTOS
        // ------------------------------------------------
        CreditApplication app4 = CreditApplication.builder()
                .company(companies.get(3)).amount(new BigDecimal("15000.00")).termMonths(12)
                .status(CreditStatus.PENDING).creditPurpose(CreditPurpose.CAPITAL_TRABAJO)
                .build();
        // Score esperado: 0 (No hay documentos, el método calculateRiskScore debe establecerlo en 0)
        app4.calculateRiskScore();


        // ------------------------------------------------
        // Solicitud 5: REJECTED (Company 5) - Score Medio-Bajo
        // ------------------------------------------------
        CreditApplication app5 = CreditApplication.builder()
                .company(companies.get(4)).amount(new BigDecimal("400000.00")).termMonths(48)
                .status(CreditStatus.REJECTED).creditPurpose(CreditPurpose.INVERSION)
                .operatorComments("Rechazado. El modelo de riesgo superó el límite de exposición para este sector.")
                .build();

        createRiskDocument(app5, "Certificado de libre de deuda", 50, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246450/Certificado_de_Libre_Deuda_1_jjbidy.pdf");
        // Score esperado: 50
        app5.calculateRiskScore();


        // ------------------------------------------------
        // Solicitud 6: PENDING (Company 6) - Score Medio
        // ------------------------------------------------
        CreditApplication app6 = CreditApplication.builder()
                .company(companies.get(5)).amount(new BigDecimal("50000.00")).termMonths(12)
                .status(CreditStatus.PENDING).creditPurpose(CreditPurpose.COMPRA_INVENTARIO)
                .operatorComments("Solicitud retenida automáticamente. KYC de Compañía incompleto.")
                .build();

        createRiskDocument(app6, "Certificado de libre de deuda", 30, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246450/Certificado_de_Libre_Deuda_2_p4xrog.pdf");
        createRiskDocument(app6, "Extracto bancario 2025", 40, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246451/Extracto_Bancario_1_wbjau9.pdf");
        // Score esperado: 30 + 40 = 70
        app6.calculateRiskScore();


        // ------------------------------------------------
        // Solicitud 7: UNDER_REVIEW (Company 7) - Score Alto
        // ------------------------------------------------
        CreditApplication app7 = CreditApplication.builder()
                .company(companies.get(6)).amount(new BigDecimal("100000.00")).termMonths(24)
                .status(CreditStatus.UNDER_REVIEW).creditPurpose(CreditPurpose.MEJORA_INFRAESTRUCTURA)
                .operatorComments("En revisión. El analista contactó al cliente para completar documentación legal (KYC).")
                .build();

        createRiskDocument(app7, "Constancia de inscripción fiscal", 33, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246451/Constancia_de_Inscripci%C3%B3n_Fiscal_2_xtmndu.pdf");
        createRiskDocument(app7, "Estatuto social", 32, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246451/ESTATUTO_SOCIAL_1_o7zzf8.pdf");
        createRiskDocument(app7, "Poderes del representante legal", 20, "https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761246452/Poderes_del_Representante_Legal_1_qjcfij.pdf");
        // Score esperado: 33 + 32 + 20 = 85
        app7.calculateRiskScore();


        List<CreditApplication> applications = List.of(app1, app2, app3, app4, app5, app6, app7);
        creditApplicationRepository.saveAll(applications);
        log.info("✅ 7 Solicitudes de Crédito y sus documentos creados. Precarga completa.");
        return applications;
    }

    private void createRiskDocument(CreditApplication app, String name, int scoreImpact, String url) {
        // 1. Crear la entidad RiskDocument
        RiskDocument doc = RiskDocument.builder()
                .name(name)
                .scoreImpact(scoreImpact)
                .documentUrl(url)
                .build();
        app.addRiskDocument(doc);

    }

    private void seedKycVerifications(List<User> allUsers, List<Company> companies) {
        log.info("💾 Creando 18 verificaciones KYC (11 USER, 7 COMPANY) con datos fijos...");
        List<KycVerification> kycList = new ArrayList<>();

        // --- 1. KYC TIPO USER (11 Total: 8 VERIFIED, 2 PENDING, 1 REJECTED) ---

        // 8 VERIFIED (Asociados a los 2 Operadores + 6 PYME)
        // Usuarios: sramirez (0), mgimenez (1), jmartinez (2), arodriguez (3), lcruz (4), gperez (5), cfuentes (6), vgarcia (7)
        //sramirez (0)
            kycList.add(KycVerification.builder()
                    .user(allUsers.get(0))
                    .status(KycStatus.VERIFIED)
                    .entityType(KycEntityType.USER)
                    .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                    .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+0)
                    .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604910/dni-frontal-1_xnge9w.png")
                    .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604890/dni-dorso-1_shuwaj.png")
                    .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604943/selfie-1_vzyeca.jpg")
                    .verificationDate(LocalDateTime.now().minusDays(15))
                    .build());

        //mgimenez (1)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(1))
                .status(KycStatus.VERIFIED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+1)
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604912/dni-frontal-2_tiw1je.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604892/dni-dorso-2_u3zx4o.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761606019/selfie-2_aly3sv.png")
                .verificationDate(LocalDateTime.now().minusDays(20))
                .build());

        //jmartinez (2)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(2))
                .status(KycStatus.VERIFIED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+2)
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604914/dni-frontal-3_bpyugj.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604894/dni-dorso-3_i4jvgu.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604945/selfie-3_ljfe1m.jpg")
                .verificationDate(LocalDateTime.now().minusDays(18))
                .build());

        //arodriguez (3)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(3))
                .status(KycStatus.VERIFIED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+3)
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604916/dni-frontal-4_zqvbcn.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604896/dni-dorso-4_llxk6m.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604931/selfie-4_c7xfsy.png")
                .verificationDate(LocalDateTime.now().minusDays(21))
                .build());

        //lcruz(4)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(4))
                .status(KycStatus.VERIFIED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+4)
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604918/dni-frontal-5_flrfiw.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604898/dni-dorso-5_ydbgix.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604934/selfie-5_vgpkrx.png")
                .verificationDate(LocalDateTime.now().minusDays(22))
                .build());

        //gperez(5)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(5))
                .status(KycStatus.VERIFIED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+5)
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604921/dni-frontal-6_xeoswr.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604900/dni-dorso-6_gxrkqz.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604936/selfie-6_axmubd.png")
                .verificationDate(LocalDateTime.now().minusDays(50))
                .build());

        //cfuentes(6)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(6))
                .status(KycStatus.VERIFIED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+6)
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604923/dni-frontal-7_k5tivk.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604901/dni-dorso-7_hnixzm.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604937/selfie-7_gushm3.png")
                .verificationDate(LocalDateTime.now().minusDays(20))
                .build());

        //vgarcia (7)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(7))
                .status(KycStatus.VERIFIED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+7)
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604925/dni-frontal-8_ih6isp.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604904/dni-dorso-8_rrdaig.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604940/selfie-8_vhetuk.png")
                .verificationDate(LocalDateTime.now().minusDays(13))
                .build());


        // 2 PENDING (Asociados a los PYME 7 y 8)
        // Usuarios: hlopez (8), mmorales (9)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(8)) // PYME 7: Héctor López
                .status(KycStatus.PENDING)
                .entityType(KycEntityType.USER)
                .verificationNotes("Dni dorso poco visible")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b70")
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604927/dni-frontal-9_y9fkrq.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604906/dni-dorso-9_r1g2e9.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604887/selfie-9_dhexgq.png")
                .build());

        kycList.add(KycVerification.builder()
                .user(allUsers.get(9)) // PYME 8: María Morales
                .status(KycStatus.PENDING)
                .entityType(KycEntityType.USER)
                .verificationNotes("Dni frontal poco visible")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b80")
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604929/dni-frontal-10_pwjfbo.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604908/dni-dorso-10_keswqk.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604888/selfie-10_gzcakc.png")
                .build());

        // 1 REJECTED (Asociado al PYME 9)
        // Usuario: jrojas (10)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(10)) // PYME 9: Javier Rojas
                .status(KycStatus.REJECTED)
                .entityType(KycEntityType.USER)
                .verificationNotes("Documento de identidad (DNI) expirado en la fecha de envío.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b98")
                .verificationDate(LocalDateTime.now().minusDays(5))
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761607008/dni-frontal-11_sqwayx.png")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761607007/dni-dorso-11_nabv32.png")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761604888/selfie-10_gzcakc.png")
                .build());

        // --- 2. KYC TIPO COMPANY (7 Total: 5 VERIFIED, 2 PENDING) ---

        // 5 VERIFIED (Compañías 1 a 5)
        for (int i = 0; i < 5; i++) {
            kycList.add(KycVerification.builder()
                    .company(companies.get(i))
                    .status(KycStatus.VERIFIED)
                    .entityType(KycEntityType.COMPANY)
                    .verificationNotes("Estatutos y registros mercantiles verificados y aprobados.")
                    .externalReferenceId("company-mock-dca05a69-2e8b-46de-b889-69be38d65b60")
                    .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_1_ssjfnv.pdf")
                    .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_4_svhtjf.pdf")
                    .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_3_hw2nlq.pdf")
                    .verificationDate(LocalDateTime.now().minusMonths(1))
                    .build());
        }

        // 2 PENDING (Compañías 6 y 7)
        kycList.add(KycVerification.builder()
                .company(companies.get(5)) // Compañía 6
                .status(KycStatus.PENDING)
                .entityType(KycEntityType.COMPANY)
                .verificationNotes("Pendiente de presentar el último Balance de Cuentas (DJI).")
                .externalReferenceId("company-mock-dca05a69-2e8b-46de-b889-69be38d65b68")
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_1_ssjfnv.pdf")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_4_svhtjf.pdf")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_3_hw2nlq.pdf")
                .build());

        kycList.add(KycVerification.builder()
                .company(companies.get(6)) // Compañía 7
                .status(KycStatus.PENDING)
                .entityType(KycEntityType.COMPANY)
                .verificationNotes("Se requiere el acta de nombramiento del representante legal.")
                .externalReferenceId("company-mock-dca05a69-2e8b-46de-b889-69be38d65b63")
                .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_1_ssjfnv.pdf")
                .document2Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_4_svhtjf.pdf")
                .document3Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761248147/ejemplo_documento_3_hw2nlq.pdf")
                .build());

        kycVerificationRepository.saveAll(kycList);
        log.info("✅ 18 verificaciones KYC creadas y persistidas correctamente.");
    }

    private List<Company> seedCompanies(List<User> pymeUsers) {
        log.info("💾 Creando 12 Compañías y asociándolas a los usuarios PYME...");

        // Se asume que 'pymeUsers' contiene al menos 12 usuarios PYME.

        List<Company> companies = List.of(
                // Compañía 1 (Dueño: Juan Martínez) - Alto Ingreso, KYC Verified
                Company.builder()
                        .company_name("Soluciones Digitales SRL")
                        .taxId("20123456789") // CUIT/Tax ID válido
                        .annualIncome(new BigDecimal("2500000.00")) // Ingreso Alto
                        .user(pymeUsers.get(0))
                        .build(),

                // Compañía 2 (Dueño: Ana Rodríguez) - Ingreso Medio, KYC Verified
                Company.builder()
                        .company_name("Distribuidora El Faro")
                        .taxId("27876543217")
                        .annualIncome(new BigDecimal("1200000.00"))
                        .user(pymeUsers.get(1))
                        .build(),

                // Compañía 3 (Dueño: Luis Cruz) - Ingreso Medio, KYC Verified
                Company.builder()
                        .company_name("Consultora Global SpA")
                        .taxId("30654321098")
                        .annualIncome(new BigDecimal("950000.00"))
                        .user(pymeUsers.get(2))
                        .build(),

                // Compañía 4 (Dueño: Gabriela Pérez) - Ingreso Bajo, KYC Verified
                Company.builder()
                        .company_name("Alimentos Frescos E.I.R.L.")
                        .taxId("33998877665")
                        .annualIncome(new BigDecimal("450000.00"))
                        .user(pymeUsers.get(3))
                        .build(),

                // Compañía 5 (Dueño: Carlos Fuentes) - Ingreso Alto, KYC Pending
                Company.builder()
                        .company_name("Constructora Alfa")
                        .taxId("20456789013")
                        .annualIncome(new BigDecimal("3500000.00"))
                        .user(pymeUsers.get(4))
                        .build(),

                // Compañía 6 (Dueño: Valeria García) - Ingreso Medio, KYC Pending
                Company.builder()
                        .company_name("Agencia Marketing Pro")
                        .taxId("34500000004")
                        .annualIncome(new BigDecimal("800000.00"))
                        .user(pymeUsers.get(5))
                        .build(),

                // Compañía 7 (Dueño: Héctor López) - Ingreso Alto, KYC Pending
                Company.builder()
                        .company_name("Transportes Rápidos S.R.L.")
                        .taxId("23112233440")
                        .annualIncome(new BigDecimal("1800000.00"))
                        .user(pymeUsers.get(6))
                        .build(),

                // Compañía 8 (Dueño: María Morales) - Ingreso Bajo, KYC Pending
                Company.builder()
                        .company_name("Tienda de Regalos")
                        .taxId("24987654321")
                        .annualIncome(new BigDecimal("300000.00"))
                        .user(pymeUsers.get(7))
                        .build(),

                // Compañía 9 (Dueño: Javier Rojas) - Ingreso Medio
                Company.builder()
                        .company_name("Desarrollos Web 3.0")
                        .taxId("30717171714")
                        .annualIncome(new BigDecimal("1100000.00"))
                        .user(pymeUsers.get(8))
                        .build(),

                // Compañía 10 (Dueño: Sandra Díaz) - Ingreso Medio
                Company.builder()
                        .company_name("Logística Sur S.A.")
                        .taxId("27101010102")
                        .annualIncome(new BigDecimal("1500000.00"))
                        .user(pymeUsers.get(9))
                        .build(),

                // Compañía 11 (Dueño: Eduardo Castillo) - Ingreso Bajo
                Company.builder()
                        .company_name("Cafetería El Grano")
                        .taxId("27101010103")
                        .annualIncome(new BigDecimal("600000.00"))
                        .user(pymeUsers.get(10))
                        .build(),

                // Compañía 12 (Dueño: Martín González) - Ingreso Bajo
                Company.builder()
                        .company_name("Estudio Contable MX")
                        .taxId("27101010109")
                        .annualIncome(new BigDecimal("750000.00"))
                        .user(pymeUsers.get(11))
                        .build()
        );

        log.info("✅ 12 Compañías creadas.");
        return companyRepository.saveAll(companies);
    }

    private List<User> seedPymeUsers() {
        log.info("💾 Creando 15 usuarios con rol PYME...");
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASS);

        List<User> pymeUsers = List.of(
                // PYME 1: Dueño de empresa verificada (Compañía 1)
                User.builder()
                        .username("juan.martinez@pyme1.com")
                        .password(encodedPassword)
                        .email("juan.martinez@pyme1.com")
                        .firstName("Juan")
                        .lastName("Martínez")
                        .contact("+5491145001111")
                        .birthDate(LocalDate.of(1980, 7, 10))
                        .dni("25111222")
                        .country("Argentina")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 2: Dueño de empresa verificada (Compañía 2)
                User.builder()
                        .username("ana.rodriguez@pyme2.com")
                        .password(encodedPassword)
                        .email("ana.rodriguez@pyme2.com")
                        .firstName("Ana")
                        .lastName("Rodríguez")
                        .contact("+5491145002222")
                        .birthDate(LocalDate.of(1991, 1, 25))
                        .dni("36555444")
                        .country("Argentina")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 3: Dueño de empresa verificada (Compañía 3)
                User.builder()
                        .username("luis.cruz@pyme3.com")
                        .password(encodedPassword)
                        .email("luis.cruz@pyme3.com")
                        .firstName("Luis")
                        .lastName("Cruz")
                        .contact("+5491145003333")
                        .birthDate(LocalDate.of(1975, 5, 12))
                        .dni("22333444")
                        .country("Chile")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 4: Dueño de empresa verificada (Compañía 4)
                User.builder()
                        .username("gabriela.perez@pyme4.com")
                        .password(encodedPassword)
                        .email("gabriela.perez@pyme4.com")
                        .firstName("Gabriela")
                        .lastName("Pérez")
                        .contact("+5491145004444")
                        .birthDate(LocalDate.of(1983, 11, 3))
                        .dni("28777666")
                        .country("Uruguay")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 5: Dueño de empresa verificada (Compañía 5)
                User.builder()
                        .username("carlos.fuentes@pyme5.com")
                        .password(encodedPassword)
                        .email("carlos.fuentes@pyme5.com")
                        .firstName("Carlos")
                        .lastName("Fuentes")
                        .contact("+5491145005555")
                        .birthDate(LocalDate.of(1978, 2, 18))
                        .dni("24999000")
                        .country("Argentina")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 6: Dueño de empresa verificada (Compañía 6)
                User.builder()
                        .username("valeria.garcia@pyme6.com")
                        .password(encodedPassword)
                        .email("valeria.garcia@pyme6.com")
                        .firstName("Valeria")
                        .lastName("García")
                        .contact("+5491145006666")
                        .birthDate(LocalDate.of(1989, 9, 28))
                        .dni("33222111")
                        .country("Chile")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 7: Dueño de empresa verificada (Compañía 7)
                User.builder()
                        .username("hector.lopez@pyme7.com")
                        .password(encodedPassword)
                        .email("hector.lopez@pyme7.com")
                        .firstName("Héctor")
                        .lastName("López")
                        .contact("+5491145007777")
                        .birthDate(LocalDate.of(1986, 4, 5))
                        .dni("30444555")
                        .country("Argentina")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 8: Dueño de empresa NO verificada (Compañía 8)
                User.builder()
                        .username("maria.morales@pyme8.com")
                        .password(encodedPassword)
                        .email("maria.morales@pyme8.com")
                        .firstName("María")
                        .lastName("Morales")
                        .contact("+5491145008888")
                        .birthDate(LocalDate.of(1993, 10, 1))
                        .dni("38111000")
                        .country("Uruguay")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 9: Dueño de empresa NO verificada (Compañía 9)
                User.builder()
                        .username("javier.rojas@pyme9.com")
                        .password(encodedPassword)
                        .email("javier.rojas@pyme9.com")
                        .firstName("Javier")
                        .lastName("Rojas")
                        .contact("+5491145009999")
                        .birthDate(LocalDate.of(1972, 8, 19))
                        .dni("20555666")
                        .country("Chile")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 10: Dueño de empresa NO verificada (Compañía 10)
                User.builder()
                        .username("sandra.diaz@pyme10.com")
                        .password(encodedPassword)
                        .email("sandra.diaz@pyme10.com")
                        .firstName("Sandra")
                        .lastName("Díaz")
                        .contact("+5491145010000")
                        .birthDate(LocalDate.of(1984, 12, 6))
                        .dni("29777888")
                        .country("Argentina")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 11: Dueño de empresa NO verificada (Compañía 11)
                User.builder()
                        .username("eduardo.castillo@pyme11.com")
                        .password(encodedPassword)
                        .email("eduardo.castillo@pyme11.com")
                        .firstName("Eduardo")
                        .lastName("Castillo")
                        .contact("+5491145011111")
                        .birthDate(LocalDate.of(1977, 6, 22))
                        .dni("23444555")
                        .country("Chile")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 12: Dueño de empresa (Compañía 12)
                User.builder()
                        .username("martin.gonzalez@pyme12.com")
                        .password(encodedPassword)
                        .email("martin.gonzalez@pyme12.com")
                        .firstName("Martín")
                        .lastName("González")
                        .contact("+5491145012222")
                        .birthDate(LocalDate.of(1990, 3, 30))
                        .dni("35999000")
                        .country("Argentina")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 13: Usuario sin compañía asociada
                User.builder()
                        .username("patricia.fuentes@pyme13.com")
                        .password(encodedPassword)
                        .email("patricia.fuentes@pyme13.com")
                        .firstName("Patricia")
                        .lastName("Fuentes")
                        .contact("+5491145013333")
                        .birthDate(LocalDate.of(1982, 1, 1))
                        .dni("27111222")
                        .country("Argentina")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 14: Usuario sin compañía asociada
                User.builder()
                        .username("roberto.vega@pyme14.com")
                        .password(encodedPassword)
                        .email("roberto.vega@pyme14.com")
                        .firstName("Roberto")
                        .lastName("Vega")
                        .contact("+5491145014444")
                        .birthDate(LocalDate.of(1970, 11, 11))
                        .dni("18000999")
                        .country("Chile")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build(),

                // PYME 15: Usuario sin compañía asociada
                User.builder()
                        .username("laura.bravo@pyme15.com")
                        .password(encodedPassword)
                        .email("laura.bravo@pyme15.com")
                        .firstName("Laura")
                        .lastName("Bravo")
                        .contact("+5491145015555")
                        .birthDate(LocalDate.of(1994, 8, 2))
                        .dni("39333444")
                        .country("Uruguay")
                        .role(User.Role.PYME)
                        .isActive(true)
                        .build()
        );

        log.info("✅ 15 usuarios PYME creados.");
        return userRepository.saveAll(pymeUsers);
    }

    private List<User> seedOperators() {
        log.info("💾 Creando 2 usuarios OPERADOR...");
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASS);

        // Operador 1: Sofía Ramírez (Senior/Admin, Argentina)
        User operador1 = User.builder()
                .username("operador1@creditflow.com")
                .password(encodedPassword)
                .email("operador1@creditflow.com")
                .firstName("Sofía")
                .lastName("Ramírez")
                .contact("+5491130001001")
                .birthDate(LocalDate.of(1988, 3, 15))
                .dni("35123456")
                .country("Argentina")
                .role(User.Role.OPERADOR)
                .isActive(true)
                .build();

        // Operador 2: Marcelo Giménez (Analista, Chile)
        User operador2 = User.builder()
                .username("operador2@creditflow.com")
                .password(encodedPassword)
                .email("operador2@creditflow.com")
                .firstName("Marcelo")
                .lastName("Giménez")
                .contact("+56998765432")
                .birthDate(LocalDate.of(1995, 11, 20))
                .dni("40987654")
                .country("Chile")
                .role(User.Role.OPERADOR)
                .isActive(true)
                .build();

        List<User> operators = List.of(operador1, operador2);
        log.info("✅ 2 operadores creados.");
        return userRepository.saveAll(operators);
    }
}