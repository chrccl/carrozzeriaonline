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

    public static final String BOT_DATE_MESSAGE =
            """
            Scrivi la *DATA* dell’incidente nel seguente formato: *gg/mm/aaaa*.
            """;

    public static final String BOT_FALLBACK_DATE_MESSAGE =
            """
            Formato della data non corretta. Scrivi la data dell'incidente nel seguente formato *gg/mm/aaaa*.
            """;

    public static final String BOT_FULLNAME_MESSAGE = "Scrivi il tuo *NOME* e *COGNOME* in un unico messaggio.";



    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final String REVERSE_DATE_FORMAT = "dd\\MM\\yyyy";



    public static final String CARLINK_WARRANT_PATH = "/opt/tomcat/dichiarazioneCarLink_compilabile.pdf";

    public static final String SAVOIA_WARRANT_PATH = "/opt/tomcat/mandatoSavoia.pdf";

    public static final String USER_CARLINK_WARRANT_PATH_FORMAT = "/opt/tomcat/%s/CarLink.pdf";

    public static final String USER_SAVOIA_WARRANT_PATH_FORMAT = "/opt/tomcat/%s/Savoia.pdf";

    public static final String FONT_PATH = "/opt/tomcat/fonts/Birthstone-Regular.ttf";

}
