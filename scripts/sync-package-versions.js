const SENTIANCE_ORG_NAME = "@sentiance-react-native";
const SENTIANCE_MODULES = ['core', 'crash-detection', 'user-context', 'legacy'];

let newVersion = process.env.npm_config_newversion;
let isNewVersionSpecified = true;

if (!newVersion) {
  console.info('A new version number has not been specified. Falling back to the number indicated on the top level package.json');
  newVersion = process.env.npm_package_version;
  isNewVersionSpecified = false;
}

function updateVersionNumberForTopLevelModule() {
  const saveFile = require('fs').writeFileSync;
  const pkgJsonPath = `${require.main.paths[0].split('node_modules')[0]}../package.json`;
  const json = require(pkgJsonPath);

  if (json.hasOwnProperty('version')) {
    json['version'] = newVersion;
  }

  saveFile(pkgJsonPath, JSON.stringify(json, null, 2));
}

function updateVersionNumbersForModule(moduleName) {
  const saveFile = require('fs').writeFileSync;
  const pkgJsonPath = `${require.main.paths[0].split('node_modules')[0]}../packages/${moduleName}/package.json`;
  const json = require(pkgJsonPath);

  if (json.hasOwnProperty('version')) {
    json['version'] = newVersion;
  }

  if (json.hasOwnProperty('peerDependencies')) {
    const peerDeps = Object.entries(json.peerDependencies);
    if (peerDeps.length) {
      const sentianceDeps = peerDeps.filter(peerDep => {
        const [peerDepName, _] = peerDep;
        return peerDepName.startsWith(SENTIANCE_ORG_NAME);
      }).map(dep => {
        const [peerDepName, _] = dep;
        return peerDepName;
      });

      sentianceDeps.forEach(sentianceDep => {
        json.peerDependencies[sentianceDep] = newVersion;
      });
    }
  }

  saveFile(pkgJsonPath, JSON.stringify(json, null, 2));
}

if (isNewVersionSpecified) {
  updateVersionNumberForTopLevelModule();
}
SENTIANCE_MODULES.forEach(module => updateVersionNumbersForModule(module));
