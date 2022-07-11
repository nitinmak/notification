   $url = 'https://fcm.googleapis.com/fcm/send';

                $fields = array(
                    
                    'registration_ids' => $tokens,
                    'priority' => 'high', 
                    'data' => array(
                        "body" => $data['description'],
                        "message" => $data['description'],
                        "title" => $data['title'],
                        "type" => 'custom',
                        "ride_type" => 'custom',
                        "icon" => "app_icon_new",
                        "vibrate" => 1,
                        "image" => "https://images.idgesg.net/images/article/2022/05/android-notifications-100928423-large.jpg",
                        "picture" => "https://carrozcabs.com/admin/uploads/notification/".$name,
                        'largeIcon' => "https://carrozcabs.com/admin/uploads/notification/".$name,
                        "sound" => "default",
                    ),

                );

                $fields = json_encode($fields);
                $headers = array(
                    'Authorization: key=' . $fcmkey['fcmkey'],
                    'Content-Type: application/json'
                );
                
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, $url);
                curl_setopt($ch, CURLOPT_POST, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);
                $result = curl_exec($ch);

                curl_close($ch);