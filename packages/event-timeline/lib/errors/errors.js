export const E_TRANSPORT_TAG_ERROR = "E_TRANSPORT_TAG_ERROR";

class TransportTaggingError extends Error {
  constructor(message) {
    super(message);
    this.name = "TransportTaggingError";
  }
}

module.exports = {
  TransportTaggingError,
  E_TRANSPORT_TAG_ERROR
}
