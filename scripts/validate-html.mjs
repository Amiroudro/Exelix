import fs from 'node:fs';
import vm from 'node:vm';

const html = fs.readFileSync(new URL('../index.html', import.meta.url), 'utf8');
const checks = [
  ['HTML document', /<!doctype html>/i],
  ['responsive viewport', /name="viewport"/i],
  ['Vocabulary library view', /id="library"/],
  ['Review view', /id="review"/],
  ['Import and backup view', /id="backup"/],
  ['Local persistence', /localStorage/],
  ['JSON backup', /JSON\.stringify/],
  ['Import preview', /function preview\(/],
  ['Duplicate detection', /Duplicate word detected/],
  ['Due-now review filter', /value="due">Due now/],
  ['Metadata-aware search', /w\.synonyms,w\.antonyms,w\.collocations,w\.source,w\.pos/],
  ['Expanded vocabulary card fields', /Antonyms: '\+esc\(w\.antonyms\)/],
  ['Quoted-delimiter import parser', /function parseDelimitedRow\(/],
  ['Header-mapped metadata import', /collocations:get\(\['collocations','collocation'\]\)/],
  ['Legacy backup duplicate normalization', /w\.normalized\|\|norm\(w\.word\)/],
  ['Legacy storage normalization', /normalized:w\.normalized\|\|norm\(w\.word\)/],
  ['No remote script dependencies', !/<script[^>]+src=["']https?:/i.test(html)],
];
let failed = false;
for (const [name, test] of checks) {
  const ok = typeof test === 'function' ? test(html) : test instanceof RegExp ? test.test(html) : test;
  console.log((ok ? 'PASS' : 'FAIL') + '  ' + name);
  if (!ok) failed = true;
}
const scripts = [...html.matchAll(/<script>([\s\S]*?)<\/script>/gi)].map(m => m[1]);
if (!scripts.length) {
  console.log('FAIL  inline JavaScript present');
  failed = true;
} else {
  try {
    scripts.forEach(source => new vm.Script(source, { filename: 'index-inline.js' }));
    console.log('PASS  inline JavaScript syntax');
  } catch (err) {
    console.error('FAIL  inline JavaScript syntax: ' + err.message);
    failed = true;
  }
}
if (failed) process.exit(1);

const parserSource = html.match(/function parseDelimitedRow\\([\\s\\S]*?function preview\\(/);
if (!parserSource) {
  console.log('FAIL  import parser source extraction');
  failed = true;
} else {
  try {
    const parserCode = parserSource[0].replace(/function preview\\([\\s\\S]*$/, '');
    const sandbox = { norm: value => String(value ?? '').trim().toLowerCase().replace(/\\s+/g, ' ') };
    vm.runInNewContext(parserCode + '\\nthis.parseText = parseText; this.parseDelimitedRow = parseDelimitedRow;', sandbox);
    const parsed = sandbox.parseText('Word,Meaning,Example,Synonyms\\n"hello, world","a greeting","Say \\"hello\\"","hi;hey"');
    const csvOk = parsed.length === 1 && parsed[0].word === 'hello, world' && parsed[0].meaning === 'a greeting' && parsed[0].example === 'Say "hello"' && parsed[0].synonyms === 'hi;hey';
    console.log((csvOk ? 'PASS' : 'FAIL') + '  CSV quoted commas and escaped quotes');
    if (!csvOk) failed = true;
    const tsv = sandbox.parseText('Word\\tMeaning\\nbright\\tshining');
    const tsvOk = tsv.length === 1 && tsv[0].word === 'bright' && tsv[0].meaning === 'shining';
    console.log((tsvOk ? 'PASS' : 'FAIL') + '  TSV header import');
    if (!tsvOk) failed = true;
    const md = sandbox.parseText('| Word | Meaning |\\n| --- | --- |\\n| apple | fruit |');
    const mdOk = md.length === 1 && md[0].word === 'apple' && md[0].meaning === 'fruit';
    console.log((mdOk ? 'PASS' : 'FAIL') + '  Markdown table import');
    if (!mdOk) failed = true;
    const simple = sandbox.parseText('abandon — leave behind\\nreliable: trustworthy');
    const simpleOk = simple.length === 2 && simple[0].word === 'abandon' && simple[1].meaning === 'trustworthy';
    console.log((simpleOk ? 'PASS' : 'FAIL') + '  Plain-text separator import');
    if (!simpleOk) failed = true;
  } catch (err) {
    console.error('FAIL  import parser behavior: ' + err.message);
    failed = true;
  }
}

console.log('Validation checks completed.');
