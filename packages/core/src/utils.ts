export function varToString(obj: { [key: string]: any }): string {
  return Object.keys(obj)[0] ?? "";
}
