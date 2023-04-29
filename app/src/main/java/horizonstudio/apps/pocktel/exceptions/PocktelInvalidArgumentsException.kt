package horizonstudio.apps.pocktel.exceptions

open class PocktelInvalidArgumentsException: PocktelException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
