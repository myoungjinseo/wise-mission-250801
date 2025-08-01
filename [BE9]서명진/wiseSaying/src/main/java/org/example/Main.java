package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static List<WiseSaying> dummyData = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);
    private final static String PATH = "src/main/java/org/example/db/wiseSaying/";
    public static void main(String[] args) {
        System.out.println("==명령 앱==");
        String s = "";
        int i = 0;
        while(!s.equals("종료")){
            System.out.print("명령) ");
            s = sc.nextLine();
            if(s.equals("등록")){
                createWiseSaying(++i);
            }
            if(s.equals("목록")){
                System.out.println(getWiseSaying());
            }
            if(s.contains("삭제?id=")){
                Pattern pattern = Pattern.compile("\\d+");      // 패턴으로 id 값을 가져온다.
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    int id = Integer.parseInt(matcher.group());
                    deleteWiseSaying(id);
                }
            }
            if(s.contains("수정?id=")){
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    int id = Integer.parseInt(matcher.group());
                    updateWiseSaying(id);
                }
            }
            if(s.equals("빌드")){
                saveAllJson();
            }
        }
        saveJson();
    }

    // 명언을 등록하는 메소드
    public static void createWiseSaying(int i){
        System.out.print("명언 : ");
        String content = sc.nextLine();
        System.out.print("작가 : ");
        String author = sc.nextLine();
        WiseSaying wiseSaying = new WiseSaying(i, content, author);
        System.out.printf("%d 번 명언이 등록되었습니다.\n",wiseSaying.getId());
        dummyData.add(wiseSaying);
    }

    // 명언을 삭제하는 메소드, removeIf로 id 값을 찾음
    public static void deleteWiseSaying(int id){
        if(existId(id)){
            dummyData.removeIf(i -> i.getId() == id);
            System.out.printf("%d번 명령은 삭제되었습니다.\n", id);
        } else {
            System.out.printf("%d번 명언은 존재하지 않습니다.\n", id);
        }
    }

    // 명언 목록 메소드
    // 리스트 문을 복사하여 리버스 시킴 -> 직접 참고하고 싶지 않아서
    // trim을 이용하여 마지막 줄바꿈을 없앤다.
    public static String getWiseSaying(){
        StringBuilder sb = new StringBuilder();
        sb.append("번호 / 작가 / 명언").append("\n");
        sb.append("----------------------").append("\n");
        List<WiseSaying> reversed = new ArrayList<>(dummyData);
        Collections.reverse(reversed);
        for(WiseSaying ws : reversed){
            sb.append(ws.getId()).append("/ ").append(ws.getAuthor()).append("/ ").append(ws.getContent()).append("\n");
        }
        return sb.toString().trim();
    }

    // 명언을 수정하는 메소드
    // list.set을 이용하고 리스트가 어디에 있는지 찾기
    public static void updateWiseSaying(int id){
        int findId = findId(id);
        if(findId != -1){
            System.out.println("명언(기존) : " + dummyData.get(findId).getContent());

            System.out.print("명언 : ");
            String content = sc.nextLine();

            System.out.println("작가(기존) : " + dummyData.get(findId).getAuthor());

            System.out.print("작가 : ");
            String author = sc.nextLine();

            WiseSaying wiseSaying = new WiseSaying(id, content, author);

            dummyData.set(findId,wiseSaying);
        } else {
            System.out.printf("%d번 명언은 존재하지 않습니다.\n", id );
        }
    }

    // id가 존재하는 지 찾는 메소드
    public static boolean existId(int id){
        return dummyData.stream().anyMatch(i -> i.getId() == id);
    }

    // id가 어디에 있는지 찾는 메소드
    public static int findId(int id){
        for(int i = 0; i < dummyData.size(); i++){
            if(dummyData.get(i).getId() == id){
                return i;
            }
        }
        return -1;
    }

    // 파일을 쓰는 메소드
    // 라이브러리 사용 안하고 직접 json 작성
    public static void saveJson() {
        System.out.println("프로그램 다시 시작...\n");
        System.out.println("== 명언 앱 ==");
        System.out.println("명령) 목록");
        System.out.println(getWiseSaying());

        for(WiseSaying ws : dummyData){
            StringBuilder sb = new StringBuilder();
            String file = String.format(PATH + "%d.json",ws.getId());
            sb.append("{\n");
            sb.append("  \"id\": ").append(ws.getId()).append(",\n");
            sb.append("  \"content\": \"").append(ws.getContent()).append("\",\n");
            sb.append("  \"author\": \"").append(ws.getAuthor()).append("\"\n");
            sb.append("}");

            try (FileWriter fw = new FileWriter(file)){
                fw.write(sb.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (FileWriter fw = new FileWriter(PATH + "lastId.txt")){
            fw.write(String.valueOf(dummyData.getLast().getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 명언의 전체 값을 json 파일로 추출
    public static void saveAllJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for(WiseSaying ws : dummyData){
            sb.append("  {\n");
            sb.append("    \"id\": ").append(ws.getId()).append(",\n");
            sb.append("    \"content\": \"").append(ws.getContent()).append("\",\n");
            sb.append("    \"author\": \"").append(ws.getAuthor()).append("\"\n");
            sb.append("  }");
            if(dummyData.getLast().getId() != ws.getId()){
                sb.append(",");
            }
        }
        sb.append("\n]");

        String file = String.format(PATH + "data.json");
        try (FileWriter fw = new FileWriter(file)){
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }
}