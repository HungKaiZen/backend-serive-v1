package vn.tayjava.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.json.JsonParseException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions 400(@RequestBody, @RequestParam, @PathVariable)
     */
    @ExceptionHandler({
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle exception when the data invalid. (@RequestBody, @RequestParam, @PathVariable)",
                                    summary = "Handle Bad Request",
                                    value = """
                                            {
                                                 "timestamp": "2024-04-07T11:38:56.368+00:00",
                                                 "status": 400,
                                                 "path": "/api/v1/...",
                                                 "error": "Invalid Payload",
                                                 "message": "{data} must be not blank"
                                             }
                                            """
                            ))})
    })
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        if (e instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) e, errorResponse);
        } else if (e instanceof MissingServletRequestParameterException) {
            return handleMissingServletRequestParameterException((MissingServletRequestParameterException) e, errorResponse);
        } else if (e instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) e, errorResponse);
        } else {
            errorResponse.setError("Invalid Data");
            errorResponse.setMessage(e.getMessage());
        }

        return errorResponse;
    }



    /*
    * IllegalArgumentException (400 BAD REQUEST)
    * */
    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Illegal Argument Exception",
                content = @Content(mediaType = APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "400 Response",
                            summary = "Handle exception when invalid argument",
                            value = """
                                    {
                                        "timestamp": "2023-10-19T06:35:52.333+00:00",
                                        "status": "400",
                                        "path": "/api/v1/...",
                                        "error": "Invalid Argument.",
                                        "message": "Age must be over 18."
                                    }
                                    """
                    )))
    })
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                "Invalid Argument",
                e.getMessage(),
                null
        );
    }

    /*
    * JsonParseException (400 BAD REQUEST)
    * Khi JSON gửi lên không hợp lệ, bị sai cấu trúc.
    * */
    @ExceptionHandler(JsonParseException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Json Parse Exception",
                content = @Content(mediaType = APPLICATION_JSON_VALUE,
                        examples = @ExampleObject(
                                name = "400 Response",
                                summary = "Handle exception when json parse error",
                                value = """
                                        {
                                            "timestamp": "2023-10-19T06:35:52.333+00:00",
                                            "status": "400",
                                            "path": "/api/v1/...",
                                            "error": "Invalid JSON",
                                            "message": "Malformed JSON request"
                                        }
                                        """
                        )))
    })
    public ErrorResponse handleJsonParseException(JsonParseException e, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                "Invalid JSON",
                "Malformed JSON request",
                null
        );
    }

    /*
    * HttpMessageNotReadableException (400 BAD REQUEST)
    * Khi request body không đúng định dạng hoặc kiểu dữ liệu không hợp lệ.
    * */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Http Message Not Readable Exception",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                               name = "400 Response",
                               summary = "When the request body is not in the correct format or the data type is invalid",
                                value = """
                                        {
                                          "timestamp": "2023-10-19T06:35:52.333+00:00",
                                            "status": "400",
                                            "path": "/api/v1/...",
                                            "error": "Malformed Request",
                                            "message": "Invalid request body format"
                                        }
                                        """
                            )))
    })
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                "Malformed Request",
                "Invalid request body format",
                null
        );
    }

    /*
    * InternalAuthenticationServiceException (401 UNAUTHORIZED)
    * */
//    @ExceptionHandler(InternalAuthenticationServiceException.class)
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Internal Authentication Service Exception",
//                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
//                            examples = @ExampleObject(
//                                    name = "401 Response",
//                                    summary = "When authenticate fail",
//                                    value = """
//                                        {
//                                          "timestamp": "2023-10-19T06:35:52.333+00:00",
//                                            "status": "401",
//                                            "path": "/api/v1/...",
//                                            "error": "Authentication Failed",
//                                            "message": "Username or password in correct"
//                                        }
//                                        """
//                            )))
//
//    })
//    public ErrorResponse handleInternalAuthException(InternalAuthenticationServiceException e, WebRequest request) {
//        return new ErrorResponse(
//                new Date(),
//                HttpStatus.UNAUTHORIZED.value(),
//                request.getDescription(false).replace("uri=", ""),
//                "Authentication Failed",
//                e.getMessage()
//        );
//    }


    /*
    * AccessDeniedException (403 FORBIDDEN)
    * */
    @ExceptionHandler(AccessDeniedException.class)
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "403", description = "Access Denied Exception",
//                content = {@Content(mediaType = APPLICATION_JSON_VALUE,
//                        examples = @ExampleObject(
//                                name = "403 Response",
//                                summary = "Handle exception when user dot not have access",
//                                value = """
//                                        {
//                                            "timestamp": "2023-10-19T06:35:52.333+00:00",
//                                            "status": 403,
//                                            "path": "/api/v1/...",
//                                            "error": "Access denied",
//                                            "message": "You do not have permission to access this resource"
//                                        }
//                                        """
//                        ))})
//    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Access Denied Exception",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "403 Response",
                                    summary = "Handle exception when user dot not have access",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 404,
                                              "path": "/api/v1/...",
                                              "error": "Access denied",
                                              "message": "You do not have permission to access this resource"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                HttpStatus.FORBIDDEN.value(),
                request.getDescription(false).replace("uri=", ""),
                "Access Denied",
                "You do not have permission to access this resource",
                null
        );
    }

    /**
     * Handle Resource Not Found Exception (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle exception when resource not found",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 404,
                                              "path": "/api/v1/...",
                                              "error": "Not Found",
                                              "message": "{data} not found"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", ""),
                "Resource Not Found",
                e.getMessage(),
                null
        );
    }


    /*
    * HttpRequestMethodNotSupportedException (405 METHOD NOT ALLOWED)
    *
    * */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "METHOD NOT ALLOWED",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "405 Response",
                                    summary = "Handle exception when incorrect HTTP method call.",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 405,
                                              "path": "/api/v1/...",
                                              "error": "Method Not Allowed",
                                              "message": "This endpoint does not support the " + e.getMethod() + " method"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request.getDescription(false).replace("uri=", ""),
                "Method Not Allowed",
                "This endpoint does not support the " + e.getMethod() + " method",
                null
        );
    }


    /**
     * Handle Duplicate Key Exception (409)
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "409 Response",
                                    summary = "Handle exception when input data is conflicted",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 409,
                                              "path": "/api/v1/...",
                                              "error": "Conflict",
                                              "message": "{data} exists, Please try again!"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleDuplicateKeyException(DuplicateKeyException e, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                HttpStatus.CONFLICT.value(),
                request.getDescription(false).replace("uri=", ""),
                "Duplicate Key Error",
                e.getMessage(),
                null
        );
    }

    /**
     * Handle Internal Server Errors (500)
     */
//    @ExceptionHandler(Exception.class)
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "500", description = "Internal Server Error",
//                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
//                            examples = @ExampleObject(
//                                    name = "500 Response",
//                                    summary = "Handle exception when internal server error",
//                                    value = """
//                                            {
//                                              "timestamp": "2023-10-19T06:35:52.333+00:00",
//                                              "status": 500,
//                                              "path": "/api/v1/...",
//                                              "error": "Internal Server Error",
//                                              "message": "Connection timeout, please try again"
//                                            }
//                                            """
//                            ))})
//    })
//    public ErrorResponse handleGlobalException(Exception e, WebRequest request) {
//        return new ErrorResponse(
//                new Date(),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                request.getDescription(false).replace("uri=", ""),
//                "Internal Server Error",
//                "An unexpected error occurred. Please try again later.",
//                null
//        );
//    }

    // ----------------- PRIVATE METHODS FOR VALIDATION HANDLING -----------------

    private ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, ErrorResponse errorResponse) {
        errorResponse.setError("Invalid Payload");

        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        errorResponse.setMessage("Validation failed");
        errorResponse.setErrors(errors);
        return errorResponse;
    }

    private ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e, ErrorResponse errorResponse) {
        errorResponse.setError("Invalid Parameter");
        errorResponse.setMessage("Missing parameter: " + e.getParameterName());
        return errorResponse;
    }

    private ErrorResponse handleConstraintViolationException(ConstraintViolationException e, ErrorResponse errorResponse) {
        errorResponse.setError("Invalid Parameter");

        List<String> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.toList());

        errorResponse.setMessage("Validation failed");
        errorResponse.setErrors(errors);
        return errorResponse;
    }
}

