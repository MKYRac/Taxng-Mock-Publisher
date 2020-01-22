package taxng.azure.mockpublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

@Controller
public class MsgController {
	
	@Value("${output.queue}")
	private String outputQueue;
	
	@Value("${account.name}")
	private String accountName;
	
	@Value("${protocol}")
	private String protocol;
	
	@Value("${account.key}")
	private String accountKey;
	
	private static final Logger log = LoggerFactory.getLogger(MsgController.class);
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping(value = "/inmsg", produces = {"application/json"})
	@ResponseBody
	public String index(@RequestParam(value = "txmsg", required = false) String taxMessage) {
		
		final String storageConnectionString =
			    "DefaultEndpointsProtocol=" + protocol + ";" +
			    "AccountName=" + accountName + ";" +
			    "AccountKey=" + accountKey;
		if(taxMessage != null) {
			try
			{
			    CloudStorageAccount storageAccount =
			       CloudStorageAccount.parse(storageConnectionString);
			   CloudQueueClient queueClient = storageAccount.createCloudQueueClient();
			   CloudQueue queue = queueClient.getQueueReference(outputQueue);
			   CloudQueueMessage message = new CloudQueueMessage(taxMessage);
			   queue.addMessage(message);
			   log.info("<<< " + taxMessage + " successfully sent to queue " + outputQueue + " >>>");
			   return "<<< " + taxMessage + " successfully sent to queue " + outputQueue + " >>>";
			}
			catch (Exception e)
			{
			    log.error(e.toString());
			    return e.toString();
			}
		} else {
			return "Please enter an XML Tax Message to get processed.";
		}
		
	}

}
