# Exelix vocabulary import formats

Exelix currently accepts CSV (comma-separated) and TSV (tab-separated) files in the Android import flow. The import parser expects a header row and at least one data row.

## Recommended columns

| Header | Optional? | Meaning |
|---|---|---|
| Word | Required | Vocabulary entry |
| Bangla Meaning | Optional | বাংলা অর্থ |
| English Definition | Optional | Short English definition |
| Part of Speech | Optional | noun, verb, adjective, etc. |
| Example | Optional | Example sentence |
| Synonyms | Optional | Similar words |
| Antonyms | Optional | Opposite words |
| Collocations | Optional | Common word combinations |
| Source | Optional | Exam/book/source reference |
| Tags | Optional | Topic or category |

Header aliases are supported, such as `term`, `vocabulary`, `meaning`, `bangla`, `pos`, `sentence`, `synonym`, `antonym`, `exam`, and `category`.

## CSV sample

```csv
Word,Bangla Meaning,English Definition,Part of Speech,Example,Synonyms,Antonyms,Collocations,Source,Tags
abundant,প্রচুর,Available in large quantities,adjective,"The region has abundant rainfall.",plentiful,scarce,abundant resources,Sample,Environment
benevolent,দয়ালু,Kind and generous,adjective,"A benevolent person helps others.",kind,cruel,benevolent act,Sample,Character
```

## Import safety

- Review the preview before confirming; it reports existing-library duplicates and duplicates within the selected file.
- Import text is capped at 2 MB and 10,000 vocabulary rows to avoid excessive memory use.
- Existing normalized words are skipped; the importer does not overwrite your entries.
- Imported records are assigned to an import batch.
- CSV quotation supports commas inside quoted cells and escaped double quotes.
- This parser is for CSV/TSV, not XLSX Excel workbooks yet.
- The new parser and preview workflow still require CI build verification and on-device testing.
