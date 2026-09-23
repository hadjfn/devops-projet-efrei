package faria.sasikumar.sylla.myfss.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler({TypeMismatchException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidRequest(Exception exception, Model model) {
        model.addAttribute("errorMessage", "La demande contient une valeur invalide.");
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(RuntimeException exception, Model model) {
        log.error("Unexpected application failure", exception);
        model.addAttribute("errorMessage", "Une erreur est survenue. Veuillez réessayer plus tard.");
        return "error";
    }
}
