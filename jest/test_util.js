export function runOnEachPlatform(callback) {
  it.each(['android', 'ios'])
  ("when running on %s", callback);
}

export function allEqual(arr) {
  return arr.every(val => val === arr[0]);
}
