package com.tuempresa.creditflow.creditflow_api.configs;

import com.tuempresa.creditflow.creditflow_api.enums.CreditPurpose;
import com.tuempresa.creditflow.creditflow_api.enums.CreditStatus;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.model.Company;
import com.tuempresa.creditflow.creditflow_api.model.CreditApplication;
import com.tuempresa.creditflow.creditflow_api.model.KycVerification;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.CompanyRepository;
import com.tuempresa.creditflow.creditflow_api.repository.CreditApplicationRepository;
import com.tuempresa.creditflow.creditflow_api.repository.KycVerificationRepository;
import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
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

            // 4. Crear Solicitudes de Crédito (usando las primeras 7 Compañías)
            seedCreditApplications(companies.subList(0, 7));

            log.info("--- ✅ Precarga de datos finalizada. ---");
        } else {
            log.info("--- ⏭️  Base de datos ya poblada. Omitiendo Seeding. ---");
        }
    }

    private void seedCreditApplications(List<Company> companies) {
        log.info("💾 Creando 7 Solicitudes de Crédito...");

        List<CreditApplication> applications = List.of(
                CreditApplication.builder()
                        .company(companies.get(0))
                        .amount(new BigDecimal("250000.00")) // Monto Alto
                        .termMonths(36)
                        .status(CreditStatus.APPROVED)
                        .creditPurpose(CreditPurpose.INVERSION)
                        .riskScore(95)
                        .operatorComments("Aprobación automática. Score superior y KYC completo.")
                        .build(),

                // Solicitud 2: REJECTED
                // Compañía 2 (KYC VERIFIED, Ingreso Medio)
                CreditApplication.builder()
                        .company(companies.get(1))
                        .amount(new BigDecimal("30000.00")) // Monto Bajo
                        .termMonths(6)
                        .status(CreditStatus.REJECTED)
                        .creditPurpose(CreditPurpose.COMPRA_INVENTARIO)
                        .riskScore(45)
                        .operatorComments("Rechazo manual. Score de riesgo bajo post-análisis financiero.")
                        .build(),

                // Solicitud 3: UNDER_REVIEW (Alta prioridad)
                // Compañía 3 (KYC VERIFIED, Ingreso Medio)
                CreditApplication.builder()
                        .company(companies.get(2))
                        .amount(new BigDecimal("90000.00"))
                        .termMonths(18)
                        .status(CreditStatus.UNDER_REVIEW)
                        .creditPurpose(CreditPurpose.MEJORA_INFRAESTRUCTURA)
                        .riskScore(78)
                        .operatorComments("Pendiente de la firma de contrato digital.")
                        .build(),

                // Solicitud 4: PENDING (Requiere que el operador la tome)
                // Compañía 4 (KYC VERIFIED, Ingreso Bajo)
                CreditApplication.builder()
                        .company(companies.get(3))
                        .amount(new BigDecimal("15000.00"))
                        .termMonths(12)
                        .status(CreditStatus.PENDING)
                        .creditPurpose(CreditPurpose.CAPITAL_TRABAJO)
                        .riskScore(62)
                        .build(),

                // Solicitud 5: REJECTED (Por incumplimiento de KYC)
                // Compañía 5 (KYC VERIFIED, Alto Ingreso) -> Nota: Podría fallar en algún otro criterio
                CreditApplication.builder()
                        .company(companies.get(4))
                        .amount(new BigDecimal("400000.00")) // Monto Muy Alto
                        .termMonths(48)
                        .status(CreditStatus.REJECTED)
                        .creditPurpose(CreditPurpose.INVERSION)
                        .riskScore(50)
                        .operatorComments("Rechazado. El modelo de riesgo superó el límite de exposición para este sector.")
                        .build(),

                // Solicitud 6: PENDING (de KYC de Compañía)
                // Compañía 6 (KYC PENDING: falta DJI)
                CreditApplication.builder()
                        .company(companies.get(5))
                        .amount(new BigDecimal("50000.00"))
                        .termMonths(12)
                        .status(CreditStatus.PENDING)
                        .creditPurpose(CreditPurpose.COMPRA_INVENTARIO)
                        .riskScore(72)
                        .operatorComments("Solicitud retenida automáticamente. KYC de Compañía incompleto.")
                        .build(),

                // Solicitud 7: UNDER_REVIEW (Pendiente por KYC de Compañía)
                // Compañía 7 (KYC PENDING: falta Acta Representante)
                CreditApplication.builder()
                        .company(companies.get(6))
                        .amount(new BigDecimal("100000.00"))
                        .termMonths(24)
                        .status(CreditStatus.UNDER_REVIEW)
                        .creditPurpose(CreditPurpose.MEJORA_INFRAESTRUCTURA)
                        .riskScore(85)
                        .operatorComments("En revisión. El analista contactó al cliente para completar documentación legal (KYC).")
                        .build()
        );

        creditApplicationRepository.saveAll(applications);
        log.info("✅ 7 Solicitudes de Crédito creadas. La precarga de datos está completa.");
    }

    private void seedKycVerifications(List<User> allUsers, List<Company> companies) {
        log.info("💾 Creando 18 verificaciones KYC (11 USER, 7 COMPANY) con datos fijos...");
        List<KycVerification> kycList = new ArrayList<>();

        // --- 1. KYC TIPO USER (11 Total: 8 VERIFIED, 2 PENDING, 1 REJECTED) ---

        // 8 VERIFIED (Asociados a los 2 Operadores + 6 PYME)
        // Usuarios: sramirez (0), mgimenez (1), jmartinez (2), arodriguez (3), lcruz (4), gperez (5), cfuentes (6), vgarcia (7)
        for (int i = 0; i < 8; i++) {
            kycList.add(KycVerification.builder()
                    .user(allUsers.get(i))
                    .status(KycStatus.VERIFIED)
                    .entityType(KycEntityType.USER)
                    .verificationNotes("Documentación de identidad personal aprobada y vigente.")
                    .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b6"+i)
                    .verificationDate(LocalDateTime.now().minusDays(15))
                    .build());
        }

        // 2 PENDING (Asociados a los PYME 7 y 8)
        // Usuarios: hlopez (8), mmorales (9)
        kycList.add(KycVerification.builder()
                .user(allUsers.get(8)) // PYME 7: Héctor López
                .status(KycStatus.PENDING)
                .entityType(KycEntityType.USER)
                .verificationNotes("Selfie requerida: la foto de prueba de vida no fue clara.")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b70")
                .build());

        kycList.add(KycVerification.builder()
                .user(allUsers.get(9)) // PYME 8: María Morales
                .status(KycStatus.PENDING)
                .entityType(KycEntityType.USER)
                .verificationNotes("Comprobante de domicilio fiscal pendiente de subir (factura de servicio).")
                .externalReferenceId("user-mock-dca05a69-2e8b-46de-b889-69be38d65b80")
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
                    .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761092931/creditflow/kyc/kyc/05a8b784-fd46-4c82-833e-da339078fc9a/document1.pdf")
                    .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761092931/creditflow/kyc/kyc/05a8b784-fd46-4c82-833e-da339078fc9a/document2.pdf")
                    .document1Url("https://res.cloudinary.com/dkkzwhtfx/image/upload/v1761092931/creditflow/kyc/kyc/05a8b784-fd46-4c82-833e-da339078fc9a/document3.pdf")
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
                .build());

        kycList.add(KycVerification.builder()
                .company(companies.get(6)) // Compañía 7
                .status(KycStatus.PENDING)
                .entityType(KycEntityType.COMPANY)
                .verificationNotes("Se requiere el acta de nombramiento del representante legal.")
                .externalReferenceId("company-mock-dca05a69-2e8b-46de-b889-69be38d65b63")
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
                        .taxId("30712345674") // CUIT/Tax ID válido
                        .annualIncome(new BigDecimal("2500000.00")) // Ingreso Alto
                        .user(pymeUsers.get(0))
                        .build(),

                // Compañía 2 (Dueño: Ana Rodríguez) - Ingreso Medio, KYC Verified
                Company.builder()
                        .company_name("Distribuidora El Faro")
                        .taxId("33698765432")
                        .annualIncome(new BigDecimal("1200000.00"))
                        .user(pymeUsers.get(1))
                        .build(),

                // Compañía 3 (Dueño: Luis Cruz) - Ingreso Medio, KYC Verified
                Company.builder()
                        .company_name("Consultora Global SpA")
                        .taxId("770543219")
                        .annualIncome(new BigDecimal("950000.00"))
                        .user(pymeUsers.get(2))
                        .build(),

                // Compañía 4 (Dueño: Gabriela Pérez) - Ingreso Bajo, KYC Verified
                Company.builder()
                        .company_name("Alimentos Frescos E.I.R.L.")
                        .taxId("21887766551")
                        .annualIncome(new BigDecimal("450000.00"))
                        .user(pymeUsers.get(3))
                        .build(),

                // Compañía 5 (Dueño: Carlos Fuentes) - Ingreso Alto, KYC Pending
                Company.builder()
                        .company_name("Constructora Alfa")
                        .taxId("30654321098")
                        .annualIncome(new BigDecimal("3500000.00"))
                        .user(pymeUsers.get(4))
                        .build(),

                // Compañía 6 (Dueño: Valeria García) - Ingreso Medio, KYC Pending
                Company.builder()
                        .company_name("Agencia Marketing Pro")
                        .taxId("33701234567")
                        .annualIncome(new BigDecimal("800000.00"))
                        .user(pymeUsers.get(5))
                        .build(),

                // Compañía 7 (Dueño: Héctor López) - Ingreso Alto, KYC Pending
                Company.builder()
                        .company_name("Transportes Rápidos S.R.L.")
                        .taxId("30509876541")
                        .annualIncome(new BigDecimal("1800000.00"))
                        .user(pymeUsers.get(6))
                        .build(),

                // Compañía 8 (Dueño: María Morales) - Ingreso Bajo, KYC Pending
                Company.builder()
                        .company_name("Tienda de Regalos")
                        .taxId("20443322119")
                        .annualIncome(new BigDecimal("300000.00"))
                        .user(pymeUsers.get(7))
                        .build(),

                // Compañía 9 (Dueño: Javier Rojas) - Ingreso Medio
                Company.builder()
                        .company_name("Desarrollos Web 3.0")
                        .taxId("765432109")
                        .annualIncome(new BigDecimal("1100000.00"))
                        .user(pymeUsers.get(8))
                        .build(),

                // Compañía 10 (Dueño: Sandra Díaz) - Ingreso Medio
                Company.builder()
                        .company_name("Logística Sur S.A.")
                        .taxId("30998877665")
                        .annualIncome(new BigDecimal("1500000.00"))
                        .user(pymeUsers.get(9))
                        .build(),

                // Compañía 11 (Dueño: Eduardo Castillo) - Ingreso Bajo
                Company.builder()
                        .company_name("Cafetería El Grano")
                        .taxId("33112233445")
                        .annualIncome(new BigDecimal("600000.00"))
                        .user(pymeUsers.get(10))
                        .build(),

                // Compañía 12 (Dueño: Martín González) - Ingreso Bajo
                Company.builder()
                        .company_name("Estudio Contable MX")
                        .taxId("27445566778")
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