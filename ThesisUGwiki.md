# INTRODUZIONE #
## Motivazioni ##
La frenesia della vita quotidiana e gli innumerevoli impegni, personali e professionali, portano le persone a dover utilizzare ogni giorno capacità mnemoniche e organizzative complesse: tali capacità si sviluppano con l’esperienza ma, in determinate fasi della vita, possono essere limitate a causa di situazioni particolari di stress che non permettono di mantenerne l’efficienza.
In campo psico-fisiologico recenti studi correlano lo stress con la difficoltà di trasferire informazioni dalla memoria a breve termine a quella a medio termine.
Questa difficoltà spesso genera una riduzione dell’efficienza personale poiché le cose da fare vengono in mente in modo disordinato e non pianificato.  Chiunque ha sicuramente sperimentato situazioni di disagio dovute al non ricordare nel momento giusto le azioni da portare a termine (o a totalmente dimenticarle), sprechi di tempo dovuti al dover ripercorrere una strada, al dover tornare in un luogo dove si è stati poco prima o dove non si è stati ma al quale si è passati vicini.
È nostra opinione che questo fenomeno potrebbe essere tenuto sotto controllo fornendo ai soggetti puntuali informazioni relative a quali attività possono essere efficientemente svolte nel contesto in cui il soggetto si trova.

## Progetto ##
Il presente progetto si prefigge l’obiettivo di realizzare un sistema che, con l’ausilio di smartphone Android, agevoli l’utilizzatore ad affrontare quotidianamente le situazioni descritte.
Uno smartphone è un cellulare in grado di svolgere molteplici compiti in quanto dotato di una serie di sensori e dispositivi che permettono un’interazione con l’utente molto proficua, in particolare: antenne GPS integrate che permettono di geolocalizzare in qualsiasi momento il terminale, connessioni a banda larga, sistemi di acquisizione vocale che consentono di comandare il cellulare attraverso il semplice utilizzo della voce.

Il nostro sistema permette all’utilizzatore di memorizzare:
<ul>
<li>event: appuntamenti geolocalizzati da eseguire in un determinato luogo e in un determinato orario</li>
<li>task: attività da soddisfare, come ad esempio prendere il latte, che possono essere portate a termine in diversi luoghi entro una certa deadline.</li></ul>
Grazie all’utilizzo del sistema GPS, quando l’utente si troverà ad una certa distanza dai punti di interesse (negozi, ristoranti, ufficio quant'altro di necessità) nei quali poter soddisfare uno o più dei task memorizzati, il sistema lo segnalerà attraverso delle notifiche.
Uno dei significativi valori aggiunti del sistema e' la capacita' di associare le keyword contenute nel task utente alle locazioni presenti sulla mappa in modo automatico, senza richiedere all'utente di effettuare esplicitamente, lui in prima persona, tale associazione ma ragionando, invece, in modo autonomo.
Le notifiche sono di diverse tipologie (audio, vocali, visuali, vibrazioni) che possono essere scelte, anche in combinazione, dall’utente stesso a seconda delle proprie esigenze e preferenze.
Grazie all'ausilio di questo sistema, l’utilizzatore può concentrarsi in modo più sereno e rilassato in altre attività, limitando così non solo la concentrazione di informazioni da tenere a mente e pianificare ma anche, conseguentemente, il livello di stress ad essa collegato.

# ARCHITETTURA #
Il sistema adotta un'architettura client-server.

Il server che è connesso a più client via HTTP è responsabile della estesa computazione.

Ogni mobile client, d'altra parte, è responsabile attraverso job relativamente leggeri di fornire un'interfaccia utente e di tener traccia delle posizioni dell'utente.

Il server è anche connesso a un provider (correntemente Google) per fornire servizi aggiuntivi nella nostra applicazione, come
reperire le mappe e trovare posti di interesse dove poter soddisfare i propri task grazie a Google Maps,ecc.


# PERSONE CHE HANNO CONTRIBUITO AL PROGETTO #
'''Prof.Ing. Mauro Migliardi'''

'''Ing.Marco Gaudina'''


Tesisti: al momento i tesisti che si sono occupati di questo progetto si sono tutti laureati

<br>
Laureati:<br>
<ul>
<li>Giorgio Ravera - ''Università degli studi di Genova'', laurea specialistica in Ing.Informatica, 31/10/2008</li>
<li>Petrus Prasetyo Anggono - ''Università degli studi di Genova'', laurea magistrale in Ing.Informatica, 23/07/2010</li>
<li>Lorenzo Andretta - ''Università degli studi di Genova'', laurea triennale in Ing.Informatica, 11/03/2011</li>
<li>Guido Geloso - ''Università degli studi di Genova'', laurea specialistica in Ing.Informatica, 11/03/2011</li>
<li>Alessio Toso - ''Università degli studi di Padova'', laurea specialistica in Ing.Informatica, 15/03/2011</li>
<li>Anuska Benacchio - ''Università degli studi di Padova'', laurea magistrale in Ing.Informatica, 24/10/2011</li>
<li>Mirco Furlan - ''Università degli studi di Padova'', laurea magistrale in Ing.Informatica, 24/10/2011</li>
</ul>