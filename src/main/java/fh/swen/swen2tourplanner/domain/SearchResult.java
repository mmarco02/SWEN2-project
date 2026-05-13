package fh.swen.swen2tourplanner.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private List<Tour> foundTours;
    private List<TourLog> foundLogs;
}
