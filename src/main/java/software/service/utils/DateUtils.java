package software.service.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe di utilità per la gestione delle date.
 */
public class DateUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Formatta una data in stringa nel formato dd/MM/yyyy.
     * 
     * @param date La data da formattare
     * @return La stringa formattata
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }
    
    /**
     * Formatta un orario in stringa nel formato HH:mm.
     * 
     * @param time L'orario da formattare
     * @return La stringa formattata
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }
    
    /**
     * Formatta un timestamp in stringa nel formato dd/MM/yyyy HH:mm.
     * 
     * @param dateTime Il timestamp da formattare
     * @return La stringa formattata
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }
    
    /**
     * Converte una stringa in data nel formato dd/MM/yyyy.
     * 
     * @param dateStr La stringa da convertire
     * @return La data convertita, o null se la stringa non è valida
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Converte una stringa in orario nel formato HH:mm.
     * 
     * @param timeStr La stringa da convertire
     * @return L'orario convertito, o null se la stringa non è valida
     */
    public static LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Converte una stringa in timestamp nel formato dd/MM/yyyy HH:mm.
     * 
     * @param dateTimeStr La stringa da convertire
     * @return Il timestamp convertito, o null se la stringa non è valida
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Calcola la durata in minuti tra due orari.
     * 
     * @param start L'orario di inizio
     * @param end L'orario di fine
     * @return La durata in minuti
     */
    public static long durationInMinutes(LocalTime start, LocalTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }
    
    /**
     * Genera una lista di date nell'intervallo specificato.
     * 
     * @param startDate La data di inizio
     * @param endDate La data di fine
     * @return La lista di date
     */
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        
        return dates;
    }
}