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
}
