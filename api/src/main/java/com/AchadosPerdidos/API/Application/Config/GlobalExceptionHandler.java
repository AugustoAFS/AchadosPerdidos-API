package com.AchadosPerdidos.API.Application.Config;

import com.AchadosPerdidos.API.Application.DTOs.Response.ErrorResponseDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.ErrorResponseDTO.FieldErrorDTO;
import com.AchadosPerdidos.API.Application.Exception.BusinessException;
import com.AchadosPerdidos.API.Application.Exception.ConflictException;
import com.AchadosPerdidos.API.Application.Exception.ResourceNotFoundException;
import com.AchadosPerdidos.API.Application.Exception.StorageException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<FieldErrorDTO> fieldErrors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = (error instanceof FieldError fe) ? fe.getField() : error.getObjectName();
                    String message = error.getDefaultMessage();
                    return new FieldErrorDTO(field, message);
                })
                .toList();

        ErrorResponseDTO body = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Um ou mais campos estão inválidos",
                request.getRequestURI());
        body.setErrors(fieldErrors);

        log.warn("Validação falhou em {}: {}", request.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String detail = ex.getMostSpecificCause().getMessage();
        String message = detail != null && detail.contains("not one of the values accepted")
                ? "Valor de enum inválido. Verifique os valores aceitos no campo."
                : "O corpo da requisição está malformado ou em formato inválido";

        log.warn("Body inválido em {}: {}", request.getRequestURI(), detail);
        return ResponseEntity.badRequest().body(
                new ErrorResponseDTO(400, "Requisição inválida", message, request.getRequestURI()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String message = "Parâmetro obrigatório ausente: '" + ex.getParameterName() +
                "' (tipo: " + ex.getParameterType() + ")";
        log.warn("Parâmetro ausente em {}: {}", request.getRequestURI(), ex.getParameterName());
        return ResponseEntity.badRequest().body(
                new ErrorResponseDTO(400, "Parâmetro ausente", message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        Class<?> requiredType = ex.getRequiredType();
        String expected = requiredType != null ? requiredType.getSimpleName() : "desconhecido";
        String message = "Valor inválido para o parâmetro '" + ex.getName() +
                "': esperado " + expected + ", recebido '" + ex.getValue() + "'";
        log.warn("Tipo inválido em {}: {}", request.getRequestURI(), message);
        return ResponseEntity.badRequest().body(
                new ErrorResponseDTO(400, "Tipo inválido", message, request.getRequestURI()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.warn("Recurso não encontrado em {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDTO(404, "Não encontrado", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        log.warn("Entidade não encontrada em {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDTO(404, "Não encontrado", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandler(
            NoHandlerFoundException ex, HttpServletRequest request) {

        String message = "Rota não encontrada: " + ex.getHttpMethod() + " " + ex.getRequestURL();
        log.warn("Rota inválida: {}", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDTO(404, "Rota não encontrada", message, request.getRequestURI()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        String message = "Método HTTP '" + ex.getMethod() + "' não é suportado neste endpoint. " +
                "Métodos aceitos: " + ex.getSupportedHttpMethods();
        log.warn("Método não suportado em {}: {}", request.getRequestURI(), ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErrorResponseDTO(405, "Método não permitido", message, request.getRequestURI()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(
            ConflictException ex, HttpServletRequest request) {

        log.warn("Conflito em {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponseDTO(409, "Conflito", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {

        log.warn("Upload excedeu o tamanho máximo em {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                new ErrorResponseDTO(413, "Arquivo muito grande",
                        "O tamanho do arquivo enviado excede o limite permitido",
                        request.getRequestURI()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(
            BusinessException ex, HttpServletRequest request) {

        log.warn("Regra de negócio violada em {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponseDTO(422, "Regra de negócio", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.warn("Argumento inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponseDTO(422, "Dados inválidos", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnsupportedOperation(
            UnsupportedOperationException ex, HttpServletRequest request) {

        log.warn("Operação não suportada em {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponseDTO(422, "Operação não suportada", ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponseDTO> handleStorage(
            StorageException ex, HttpServletRequest request) {

        log.error("Falha no storage em {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                new ErrorResponseDTO(502, "Falha no armazenamento",
                        "Não foi possível processar o arquivo. Tente novamente mais tarde.",
                        request.getRequestURI()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntime(
            RuntimeException ex, HttpServletRequest request) {

        log.error("Erro interno em {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseDTO(500, "Erro interno",
                        "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex, HttpServletRequest request) {

        log.error("Exceção não mapeada em {} {}: {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseDTO(500, "Erro interno",
                        "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
                        request.getRequestURI()));
    }
}
