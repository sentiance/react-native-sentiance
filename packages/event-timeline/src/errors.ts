export const E_TRANSPORT_TAG_ERROR = "E_TRANSPORT_TAG_ERROR";

export class TransportTaggingError extends Error {
  constructor(message: string) {
    super(message);
    this.name = this.constructor.name;
    Object.setPrototypeOf(this, TransportTaggingError.prototype);
  }
}

export function isErrorWithCodeAndMsg(e: unknown): e is { code: string; message: string } {
  return (
    typeof e === "object" &&
    e !== null &&
    typeof (e as { code?: unknown }).code === "string" &&
    typeof (e as { message?: unknown }).message === "string"
  );
}
