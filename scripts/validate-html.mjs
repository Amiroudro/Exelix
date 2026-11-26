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
console.log('Validation checks completed.');
