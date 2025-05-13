package it.chrccl.carrozzeriaonline.model;

public class Constants {

    public static final String BOT_START_MESSAGE =
            """
            Mandaci *FOTO* e *VIDEO* dei danni e della scena dell'incidente con targhe visibili.
            
            *DOCUMENTI* di veicoli e conducenti, compresi i testimoni.
            
            Inviaci anche il *CID o le DICHIARAZIONI* dei conducenti.
            
            Se presenti, allega anche i dati forniti dalle autorità: *GENERALITÀ/VERBALE*.
            
            Inviaci quindi minimo 3 file multimediali o un pdf con tutto il necessario.
            
            Se hai bisogno di *Soccorso Stradale*, chiama uno dei seguenti numeri:
            - *ACI*: 803116
            - *Europe Assistance*: 803803
            - *Carabinieri*: 112
            """;

    public static final String BOT_OUT_OF_ORDER_ATTACHMENT = "Allegato aggiunto correttamente.";

    public static final String BOT_DATE_MESSAGE =
            """
            Scrivi la *DATA* dell’incidente nel seguente formato: *gg/mm/aaaa*.
            """;

    public static final String BOT_FALLBACK_DATE_MESSAGE =
            """
            Data non riconosciuta, scrivi la data dell'incidente nel seguente formato *gg/mm/aaaa*.
            """;

    public static final String BOT_FULLNAME_MESSAGE = "Scrivi il tuo *NOME* e *COGNOME* in un unico messaggio.";

    public static final String BOT_CF_OR_PIVA_MESSAGE = "Scrivi il tuo *CODICE FISCALE* o *P. IVA*.";

    public static final String BOT_FALLBACK_CF_OR_PIVA_MESSAGE = "Codice Fiscale o Partita IVA non riconosciuto. Riprova!";

    public static final String BOT_CAR_LICENCE_MESSAGE = "Scrivi la *TARGA* della tua auto.";

    public static final String EXPIRED = "EXPIRED";

    public static final String BOT_SENDING_OTP_MESSAGE =
            """
            Ti abbiamo inviato un codice via SMS valido 15 minuti. Scrivilo qui in chat per autorizzarci al trattamento dei dati da te forniti a fini esclusivamente identificativi e per confermare di voler delegare i nostri partner al recupero dei crediti relativi ai danni subiti senza alcun costo a tuo carico. Se non ti è arrivato, scrivi *RINVIA* in chat.
            In caso di concorsualità o torto nella dinamica del sinistro o decurtazioni dovute a scoperti e franchigie, potrai comunque concordare con il riparatore costi e modalità dell'intervento alle migliori condizioni a te riservate, con auto sostitutiva, ritiro e consegna a domicilio e lavaggio del veicolo.
            Visita www.carrozzeriaonline.com/privacy-policy per leggere l'informativa sulla privacy.
            """;


    public static final String BOT_FALLBACK_SENDING_OTP_MESSAGE =
            """
            Il codice che ci hai fornito è errato o scaduto. Ti abbiamo inviato nuovamente un codice via SMS valido 15 minuti. Scrivilo qui in chat per autorizzarci al trattamento dei dati da te forniti a fini esclusivamente identificativi e per confermare di voler delegare i nostri partner al recupero dei crediti relativi ai danni subiti senza alcun costo a tuo carico. Se non ti è arrivato, scrivi *RINVIA* in chat.
            In caso di concorsualità o torto nella dinamica del sinistro o decurtazioni dovute a scoperti e franchigie, potrai comunque concordare con il riparatore costi e modalità dell'intervento alle migliori condizioni a te riservate, con auto sostitutiva, ritiro e consegna a domicilio e lavaggio del veicolo.
            Visita www.carrozzeriaonline.com/privacy-policy per leggere l'informativa sulla privacy.
            """;

    public static final String BOT_FALLBACK_EXPIRED_OTP_MESSAGE =
            """
            ⚠️ Il tempo limite per completare l'incarico è scaduto. Se desideri ricominciare, scrivi AVVIA in chat per ripartire da capo.
            """;

    public static final String BOT_CAP_MESSAGE = "Scrivi il tuo *CAP* per individuare la carrozzeria più vicina a te.";

    public static final String BOT_CUSTOM_REPAIR_CENTER_CHOSEN_MESSAGE =
            "Richiesta inoltrata con successo, a breve verrai contattato dalla carrozzeria inserita.";

    public static final String BOT_CARLINK_REPAIR_CENTER_CHOSEN_MESSAGE = """
            Richiesta inoltrata con successo a %s, %s, %s.
            Per qualsiasi dubbio o informazione aggiuntiva sullo stato dell'incarico puoi chiamare: *0289618300* o\s
            scrivere a *incarichi@car-link.it*.
           \s""";

    public static final String BOT_SAVOIA_REPAIR_CENTER_CHOSEN_MESSAGE = """
            Richiesta inoltrata con successo, a breve verrai contattato da: %s, %s, %s, %s.
            """;

    public static final String BOT_REPAIR_CENTER_NOT_KNOWN_MESSAGE = "Carrozzeria richiesta non riconosciuta, riprova.";



    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final String REVERSE_DATE_FORMAT = "dd\\MM\\yyyy";

    public static final String SIGNED_DIGITALLY_STATUS = "Firmato digitalmente tramite OTP";



    public static final String CARLINK_TASKS_EMAIL = "incarichi@car-link.it";

    public static final String SAVOIA_TASKS_EMAIL = "incarichicarrozzeriaonline@gmail.com";

    public static final String TASK_EMAIL_SUBJECT = "Nuovo Incarico %s - Carrozzeria Online";

    public static final String TEMPLATE_PARTNER_TASK_EMAIL = "IncaricoPerPartner.html";

    public static final String TEMPLATE_REPAIR_CENTER_TASK_EMAIL = "IncaricoPerCarrozziere.html";

    public static final String TEMPLATE_NO_MORE_TASK_SAVOIA_REPAIR_CENTER = "IncaricoDisdettoSavoia.html";

    public static final String COMPANY_NAME_NOT_PROVIDED = "Non specificato dall'utente";



    public static final String CARLINK_WARRANT_PATH = "/opt/tomcat/warrantsTemplates/dichiarazioneCarLink_compilabile.pdf";

    public static final String SAVOIA_WARRANT_PATH = "/opt/tomcat/warrantsTemplates/mandatoSavoia.pdf";

    public static final String USER_WARRANT_DIRECTORY_PATH_FORMAT = "/var/lib/carrozzeria_online/attachments/%s";

    public static final String USER_CARLINK_WARRANT_PATH_FORMAT = "/var/lib/carrozzeria_online/attachments/%s/CarLink.pdf";

    public static final String USER_SAVOIA_WARRANT_PATH_FORMAT = "/var/lib/carrozzeria_online/attachments/%s/Savoia.pdf";

    public static final String FONT_PATH = "/opt/tomcat/fonts/Birthstone-Regular.ttf";

    public static final String WARRANT_CONTENT_TYPE = "application/pdf";


    public static final String SQUILLACE_REPORT_PATH = "/opt/tomcat/Squillace/report.txt";

    public static final String SQUILLACE_INSTAGRAM_PAGE = "https://www.instagram.com/nunziosquillace?igsh=MWc0emZ3dGx6NTUyOA%3D%3D&utm_source=qr";


    public static final String ACCEPTED_TASK_PAGE = "https://incarico-accettato.carrozzeriaonline.com/";

    public static final String TASK_ALREADY_ACCEPTED_PAGE = "https://incarico-gia-accettato.carrozzeriaonline.com/";

    public static final String REJECTED_TASK_PAGE = "https://incarico-rifiutato.carrozzeriaonline.com/";


    public static final String TWILIO_PREFIX = "whatsapp:";

    public static final String ITALY_SMS_COUNTRY_CODE = "+39";

    public static final Integer MIN_ATTACHMENTS_PER_TASK = 3;


    public static final String FALLBACK_NUMBER_FOR_OLD_TASK_ADVISE = "whatsapp:+393772713978";

}
