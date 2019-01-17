import com.common.MessagesUtils;
import com.utils.redis.service.RedisDeleteDataService;
import org.apache.log4j.Logger;

/*******************************************************************************
 * @date 2019-01-17 下午 4:18
 * @author: <a href=mailto:>黄跃然</a>
 * @Description: Redis工具类 Server入口
 ******************************************************************************/
public class RedisServer {

    private static Logger logger = Logger.getLogger(RedisServer.class);

    public static void main(String[] args) {
        try {
            if(args.length!=2){
                printUsage();
                System.exit(-1);
            }
            int type = Integer.parseInt(args[0]);
            String address=args[1];

            MessagesUtils.startupShutdownMessage(RedisServer.class, args, logger);

            switch (type){
                case 1:
                    RedisDeleteDataService.main(new String[]{args[1]});
                    break;
                    default:
                        printUsage();
                        break;
            }



        }catch (Exception e){
            printUsage();
            logger.info("start server error.",e);
        }
    }

    public static void printUsage(){
        System.out.println("java -jar RedisUtils.jar <TYPE> <address[host:port,host:port......]>");
    }

}
