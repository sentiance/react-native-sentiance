export declare const E_TRANSPORT_TAG_ERROR = "E_TRANSPORT_TAG_ERROR";
export declare class TransportTaggingError extends Error {
    constructor(message: string);
}
export declare function isErrorWithCodeAndMsg(e: unknown): e is {
    code: string;
    message: string;
};
