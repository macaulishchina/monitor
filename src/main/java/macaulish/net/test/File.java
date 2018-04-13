package macaulish.net.test;

public class File {
    /**
     * 创建文件
     * @return  是否创建成功，成功则返回true
     */
    public static boolean createFile(String path){
        Boolean bool = false;
        java.io.File file = new java.io.File(path);
        try {
            //如果文件不存在，则创建新的文件
            if(!file.exists()){
                file.createNewFile();
                bool = true;
                System.out.println("success create file,the file is "+file.getAbsolutePath());
                //创建文件成功后，写入内容到文件里
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bool;
    }

    public static void main(String args[]){
        createFile("./test.txt");
    }
}
