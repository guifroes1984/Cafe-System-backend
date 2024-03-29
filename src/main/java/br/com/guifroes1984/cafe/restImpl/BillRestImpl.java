package br.com.guifroes1984.cafe.restImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.guifroes1984.cafe.POJO.Bill;
import br.com.guifroes1984.cafe.contents.CafeConstants;
import br.com.guifroes1984.cafe.rest.BillRest;
import br.com.guifroes1984.cafe.service.BillService;
import br.com.guifroes1984.cafe.utils.CafeUtils;

@RestController
public class BillRestImpl implements BillRest {
	
	@Autowired
	BillService billService;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			return billService.generateReport(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	 @Override
	 public ResponseEntity<List<Bill>> getBills() {
	        try {
	            return billService.getBills();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return null;
	    }

	 @Override
	    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
	        try {
	            return billService.getPdf(requestMap);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return null;
	    }

	    @Override
	    public ResponseEntity<String> deleteBill(Integer id) {
	        try {
	            return billService.deleteBill(id);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

}
