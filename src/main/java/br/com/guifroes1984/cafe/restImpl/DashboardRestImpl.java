package br.com.guifroes1984.cafe.restImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.guifroes1984.cafe.rest.DashboardRest;
import br.com.guifroes1984.cafe.service.DashboardService;

@RestController
public class DashboardRestImpl implements DashboardRest {
	
	@Autowired
    DashboardService dashboardService;

	@Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }

}
