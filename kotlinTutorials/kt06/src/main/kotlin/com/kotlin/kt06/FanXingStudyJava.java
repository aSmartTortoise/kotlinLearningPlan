package com.kotlin.kt06;

class FanXingStudyJava {
    public static void main(String[] args) {
        // Java中 父类型的集合对象不能赋值给子类型集合的应用，父类型集合和子类型集合
        //属于两个不同的类型。
        MyList<CharSequence> charsequences = new MyList<>();
//        MyList<String> strings = charsequences;//编译报错

        matchRegex();

    }

    private static class MyList<T> {
        void add(T t) {

        }
    }

    private static void matchRegex() {
        String str = "八点明天昌平有雨";
        // 正则判断str是否包含8点昌平有雨或者八点昌平有雨

        boolean result1 = str.matches(".*(8点|八点).*昌平.*有雨.*");
        boolean result2 = str.matches("(8点|八点)昌平有雨");
        System.out.println("result1:" + result1 + " result2:" + result2);
    }

    private String get(String text) {
        if (text.matches("1")) {
            return "1";
        } else if (text.matches(".*明天.*昌平.*交通指数.*")) {
            return "weather_lifeIndex#search@location=&{\"district\": \"昌平区\"}" +
                    "@date=&2024-05-17" +
                    "@index_type=&交通指数";
        } else if (text.matches(".*未来两天.*昌平.*交通指数.*")) {
            return "weather_lifeIndex#search@location=&{\"district\": \"昌平区\"}" +
                    "@date_range=&{\"start\": \"2024-05-11\", \"end\": \"2024-05-12\"}" +
                    "@index_type=&交通指数";
        } else if (text.matches(".*(8点|八点).*昌平.*交通指数.*")) {
            return "weather_lifeIndex#search@location=&{\"district\": \"昌平区\"}" +
                    "@time=&2024-05-23 20:00:00" +
                    "@index_type=&交通指数";
        } else if (text.matches(".*18.*十八.*20.*二十.*昌平.*交通指数.*")) {
            return "weather_lifeIndex#search@location=&{\"district\": \"昌平区\"}" +
                    "@time_range=&{\"start\": \"2024-05-23 18:00:00\", " +
                    "\"end\": \"2024-05-23 20:00:00\"}" +
                    "@index_type=&交通指数";
        }
        else if (text.matches(".*预定.*日程.*")) {
            return "schedule#operation@operation_type=&add@date=null";
        }
        else if (text.matches(".*预定.*明天.*日程.*")) {
            return "schedule#operation@operation_type=&add@date=2024-05-11";
        }
        else if (text.matches(".*预定.*三.*3点.*日程.*")) {
            return "schedule#operation@operation_type=&add@time=2024-05-11 15:00:00";
        }
        else if (text.matches(".*预定.*王家湾.*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&add" +
                    "@time=2024-05-11 15:00:00";
        }
        else if (text.matches(".*预定.*王家湾.*购物.*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&add" +
                    "@time=2024-05-11 15:00:00" +
                    "@event=@购物";
        }

        else if (text.matches(".*删除*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&delete";
        } else if (text.matches(".*删除.*明天*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&delete" +
                    "@date=2024-05-11";
        } else if (text.matches(".*删除.*三.*3.*点*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&delete" +
                    "@time=22024-05-11 15:00:00";
        } else if (text.matches(".*删除.*王家湾.*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&delete" +
                    "@time=2024-05-11 15:00:00";
        } else if (text.matches(".*删除.*王家湾.*购物.*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&delete" +
                    "@date=2024-05-11" +
                    "@event=@购物";
        }

        else if (text.matches(".*查询*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&query";
        } else if (text.matches(".*查询.*明天*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&query" +
                    "@date=2024-05-11";
        } else if (text.matches(".*查询.*三.*3.*点*日程.*")) {
            return "schedule#operation" +
                    "@operation_type=&query" +
                    "@date=22024-05-11 15:00:00";
        }

        else if (text.matches(".*打开.*相册.*")) {
            return "systemControl_gallery_app#switch@switch_type=&open";
        } else if (text.matches(".*关闭.*相册.*")) {
            return "systemControl_gallery_app#switch@switch_type=&close";
        }

        else if (text.matches(".*打开.*USB.*usb.*相册.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&usb";
        } else if (text.matches(".*打开.*本地相册.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&local";
        } else if (text.matches(".*打开.*云端相册.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&online";
        } else if (text.matches(".*打开.*传输列表.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&transfer_list";
        } else if (text.matches(".*打开.*默认相册.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&default";
        } else if (text.matches(".*打开.*AI.*ai.*绘图相册.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&ai_drawing";
        } else if (text.matches(".*打开.*旅拍相册.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&trip_shoot";
        } else if (text.matches(".*打开.*收藏相册.*")) {
            return "systemControl_gallery#switch@switch_type=&open@tab_name=&collection";
        }

        else if (text.matches(".*浏览.*图片.*")) {
            return "systemControl_gallery_photo#switch@switch_type=&open";
        } else if (text.matches(".*退出.*浏览.*图片.*")) {
            return "systemControl_gallery_photo#switch@switch_type=&close";
        }

        else if (text.matches(".*查看.*上1张.*上一张.*图片.*")) {
            return "systemControl_gallery_photo#control@control_type=&prev";
        } else if (text.matches(".*查看.*下1张.*下一张.*图片.*")) {
            return "systemControl_gallery_photo#control@control_type=&next";
        }

        else if (text.matches(".*放大.*图片.*")) {
            return "systemControl_gallery_photo#adjust@adjust_type=&increase";
        } else if (text.matches(".*缩小.*图片.*")) {
            return "systemControl_gallery_photo#adjust@adjust_type=&decrease";
        } else if (text.matches(".*右旋转.*图片.*")) {
            return "systemControl_gallery_photo#adjust@adjust_type=&set@direction=&clockwise";
        }

        else if (text.matches(".*打开.*酷玩盒子.*")) {
            return "systemControl_app#switch@app_name=&酷玩盒子@switch_type=&open";
        } else if (text.matches(".*关闭.*酷玩盒子.*")) {
            return "systemControl_app#switch@app_name=&酷玩盒子@switch_type=&close";
        }

//        打开趣味外放：
//        关闭趣味外放：
//        打开车外喊话：
//        关闭车外喊话：
//        打开音乐灯光秀：
//        关闭音乐灯光秀：
//        打开音乐灯光秀页面：
//        关闭音乐灯光秀页面：

//        打开宠物模式：
//        关闭宠物模式：
        else if (text.matches(".*打开.*趣味外放.*")) {
            return "systemControl#switch@tab_name=&funny_exocytosis@switch_type=&open";
        } else if (text.matches(".*关闭.*趣味外放.*")) {
            return "systemControl#switch@tab_name=&funny_exocytosis@switch_type=&close";
        } else if (text.matches(".*打开.*车外喊话.*")) {
            return "systemControl#switch@tab_name=&shout_out@switch_type=&open";
        } else if (text.matches(".*关闭.*车外喊话.*")) {
            return "systemControl#switch@tab_name=&shout_out@switch_type=&close";
        } else if (text.matches(".*打开.*音乐灯光秀.*")) {
            return "systemControl#switch@tab_name=&music_light_show@switch_type=&open";
        } else if (text.matches(".*关闭.*音乐灯光秀.*")) {
            return "systemControl#switch@tab_name=&music_light_show@switch_type=&close";
        } else if (text.matches(".*打开.*音乐灯光秀页面.*")) {
            return "systemControl#switch@tab_name=&music_light_show@page_name=&interface@switch_type=&open";
        } else if (text.matches(".*关闭.*关闭音乐灯光秀页面.*")) {
            return "systemControl#switch@tab_name=&music_light_show@page_name=&interface@switch_type=&close";
        }

        else if (text.matches(".*打开.*宠物模式.*")) {
            return "systemControl#switch@tab_name=&pets@switch_type=&open";
        } else if (text.matches(".*关闭.*宠物模式.*")) {
            return "systemControl#switch@tab_name=&pets@switch_type=&close";
        }

//        打开副驾观影模式：
//        关闭副驾观影模式：
        else if (text.matches(".*打开.*副驾观影模式.*")) {
            return "systemControl_watchMovies#switch@position=&first_row_right@switch_type=&open";
        } else if (text.matches(".*关闭.*副驾观影模式.*")) {
            return "systemControl_watchMovies#switch@position=&first_row_right@switch_type=&close";
        }


//        登录 退出登录
        else if (text.matches(".*登录.*账号.*")) {
            return "systemControl_personalCenter_account#control@control_type=&login";
        } else if (text.matches(".*退出.*账号.*")) {
            return "systemControl_personalCenter_account#control@control_type=&logout";
        }








        return "";
    }

}

