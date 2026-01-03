# GoverLens

Η GoverLens αποτελεί μια εφαρμογή η οποία δίνει την ευκαιρία στον απλό πολίτη να αναλάβει  προθυπουργικά καθήκοντα και να αισθανθεί την αίγλη της εξουσίας.Παρόλ’ αυτά,μια τέτοια θέση συνεπάγεται με ποικίλα εμπόδια και υποχρεώσεις όπου πρέπει να φέρει εις πέρας το άτομο.Άραγε,πόσο εύκολη είναι η διαχείριση μια τέτοιας θεσης η οποία επηρεάζει άμεσα την κοινωνικό-οικονομική πορεία ολόκληρης της χώρας ;Το άτομο,μέσω της εφαρμογής θα έχει πρόσβαση στους Ελληνικούς κρατικούς προϋπολογισμούς των τελευταίων έξη διαδοχικών ετών.Έχοντας μια εικόνα για την πρακτική διαχείρησης των εσόδων και των εξόδων της χώρας ,καθώς και τον τρόπο χρηματοδότησης των διάφορων φορέων,δίνεται η δυνατότητα στο άτομο να υφάνει τη μαεστρία του προϋπολογισμού.Με λίγα λόγια ,ο χρήστης πλέκει τον προϋπολογισμό  βάσει των προσωπικών του βιωμάτων και αντιλήψεων ώστε να σχεδιάσει μια υποθετική εικόνα της οικονομικης κατάστασης της χώρας .Πώς το άτομο θα δομήσει τον οικονομικό χάρτη της χώρας ;Ίσως  η ελευθερία του πολιτή στην αρχιτεκτονική του προϋπολογισμού επιφέρει ανεπανόρθωτες ζημίες στη σημερινή εικόνα της Ελλάδας ;

```text
404_NOT_FOUND/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── GUI/
│   │   │   │   ├── ActionSelectionPanel.java
│   │   │   │   ├── BudgetViewPanel.java
│   │   │   │   ├── ChartRenderer.java
│   │   │   │   ├── ComparisonPanel.java
│   │   │   │   ├── ComparisonService.java
│   │   │   │   ├── DataEditorPanel.java
│   │   │   │   ├── FinanceChartPanel.java
│   │   │   │   ├── GraphDataImporter.java
│   │   │   │   ├── LogViewerPanel.java
│   │   │   │   ├── MainFrame.java
│   │   │   │   ├── ProjectSelectionPanel.java
│   │   │   │   ├── WelcomePanel.java
│   │   │   │   └── YearSelectionPanel.java
│   │   │   │
│   │   │   ├── dao/
│   │   │   │   ├── CashFlow.java
│   │   │   │   ├── CashFlowDao.java
│   │   │   │   ├── Foreis.java
│   │   │   │   ├── ForeisDao.java
│   │   │   │   ├── Log.java
│   │   │   │   └── LogDao.java
│   │   │   │
│   │   │   ├── database/
│   │   │   │   ├── DataImporter.java
│   │   │   │   └── DatabaseSetup.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── CashFlowCompareDto.java
│   │   │   │   └── ForeasCompareDto.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── CashFlowService.java
│   │   │   │   ├── ForeisService.java
│   │   │   │   ├── LogService.java
│   │   │   │   ├── ScenarioCashflowService.java
│   │   │   │   ├── ScenarioForeisService.java
│   │   │   │   └── SimulationService.java
│   │   │   │
│   │   │   └── util/
│   │   │       ├── DbExistsChecker.java
│   │   │       └── ValidationUtils.java
│   │   │
│   │   └── resources/
│   │       ├── data/
│   │       │   ├── B21Esoda.csv - B26Esoda.csv
│   │       │   ├── B21Exoda.csv - B26Exoda.csv
│   │       │   └── B21Foreis.csv - B26Foreis.csv
│   │       │
│   │       └── db/
│   │           └── originalDB.db
│   │
│   └── test/
│       └── java/
│           ├── dao/
│           │   ├── CashFlowDaoTest.java
│           │   ├── CashFlowTest.java
│           │   ├── ForeisDaoTest.java
│           │   ├── ForeisTest.java
│           │   └── LogDaoTest.java
│           │
│           ├── database/
│           │   ├── DataImporterTest.java
│           │   └── DatabaseSetupTest.java
│           │
│           ├── dto/
│           │   ├── CashFlowCompareDto.java
│           │   └── ForeasCompareDto.java
│           │
│           ├── service/
│           │   ├── CashFlowServiceTest.java
│           │   ├── ForeisServiceTest.java
│           │   ├── LogServiceTest.java
│           │   └── SimulationServiceTest.java
│           │
│           ├── util/
│           │   ├── DbExistsCheckerTest.java
│           │   └── ValidationUtilsTest.java
│           │
│           └── resources/data/
│               ├── B23EsodaTEST.csv
│               ├── B23ExodaTEST.csv
│               └── B23ForeisTEST.csv
│
├── BackgroundPhoto.jpg
├── GoverLensLogo.jpg
├── README.md
├── checkstyle.xml
├── pom.xml
└── .gitignore
```