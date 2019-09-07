package com.lua.luanegra.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.lua.luanegra.R;
import com.lua.luanegra.tools.OnlineService;

import java.util.Locale;

public class TermosDeUtilizacaoActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void setTheme(int resId) {
        SharedPreferences prefs = getSharedPreferences("AppTheme", Context.MODE_PRIVATE);
        String tema = " ";
        tema = prefs.getString("AppTheme", " ");
        if(tema != " "){
            if(tema.equals("light")){
                super.setTheme(R.style.AppTheme_Light);
            }else {
                super.setTheme(R.style.AppTheme);
            }
        }else {
            super.setTheme(R.style.AppTheme_Light);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_de_utilizacao);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getResources().getString(R.string.termos_utilizacao));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final TextView termosdeutilizacaoText = findViewById(R.id.termosdeutilizacaoTextView);
        MaterialButton okButton = findViewById(R.id.buttonOKTermos);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(Locale.getDefault().getCountry().equals("PT") ){
            termosdeutilizacaoText.setText("\n\n✶ Termos de utilização e condições da comunidade " + getString(R.string.app_name) +  "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "✶ Termos de utilização e condições ✶\n" +
                    "\n" +
                    "- Falta de respeito = Block;\n" +
                    "- Uso abusivo de notificações é considerado falta de respeito;\n" +
                    "- Barulhos incomodativos de fundo na Call (ex: Televisão) é considerado falta de respeito;\n" +
                    "- Uso abusivo de palavrões é considerado falta de respeito;\n" +
                    "- Utilizador que esteja bloqueado em 5 salas privadas, fica bloqueado na aplicação;\n" +
                    "- Utilizador que tenha 5 advertências por conteúdos impróprios em Hall Of Fame e YouTube = Block;\n" +
                    "- Não é permitido qualquer tipo de conteúdo pornográfico;\n" +
                    "- Companheirismo, boa atitude e bom humor são pontos chave;\n" +
                    "\n" +
                    "* As permissões pedidas ao utilizador depois do Sign In servem para enviar e receber ficheiros nas salas e fazer chamadas, se não forem aceites a app devera funcionar normalmente sem contar com essas mesmas funcionalidades.\n" +
                    "* De utilizador para admin e de admin para superadmin as funcionalidades são herdadas.\n" +
                    "* Não existe qualquer tipo de publicidade.\n" +
                    "* Encriptação ponta a ponta nas mensagens privadas e nas salas privadas;\n" +
                    "* Toda a informação que é removida da base de dados É MESMO REMOVIDA NÃO HAVENDO MANEIRA DE A RECUPERAR.\n" +
                    "* Aplicação compatível com versões do android superiores a 7.0;\n" +
                    "* Aplicação em desenvolvimento, poderá conter bugs;\n" +
                    "* Serviço para detetar quando os utilizadores iniciam a app e fecham a app;\n" +
                    "* Não nos responsabiliza-mos por qualquer perca de informação na aplicação;\n" +
                    "* A associação a patrono pode levar de 24h a 48h;\n" +
                    "* Linguagens – EN e PT;\n" +
                    "\n" +
                    "\n" +
                    "ॐ Encriptação ponta a ponta ॐ\n" +
                    "\n" +
                    "-  Ao criar chat gera uma chave no telemóvel -> Grava a chave no telemóvel -> envia por notificação ao outro utilizador -> Ao receber a notificação grava a chave no telemóvel e não mostra a notificação;\n" +
                    "- A chave não é gravada em mais nenhum lado sem ser no próprio telemóvel; \n" +
                    "\n" +
                    "\n" +
                    "ॐ Salas de Temáticas e Mensagens Privadas ॐ\n" +
                    "\n" +
                    "- Cada utilizador pode enviar uma notificação a cada 20 minutos;\n" +
                    "- Sistema de chamadas integrado com receção de notificação por cada utilizador que entrar na chamada;\n" +
                    "- Envio de qualquer ficheiro com limite de tamanho de 5mb\n" +
                    "- Utilização de um Serviço ao iniciar a chamada e terminado ao desligar a chamada;\n" +
                    "- Envio de memes;\n" +
                    "- Mensagens com pré visualização de links, vídeos, sons e imagens;\n" +
                    "- Opção de download do ficheiro enviado;\n" +
                    "- Visualização da ultima mensagem recebida nas mensagens privadas;\n" +
                    "- Visualização de utilizadores em chamada e em tempo real;\n" +
                    "\n" +
                    "\n" +
                    "ॐ Cargos ॐ\n" +
                    "\n" +
                    "- Vermelho -> superadmins;\n" +
                    "- Laranja escuro -> admins;\n" +
                    "- Branco -> utilizadores;\n" +
                    "- Dourado → patronos;\n" +
                    "\n" +
                    "\n" +
                    "ॐ Utilizadores ॐ\n" +
                    "\n" +
                    "- Registo automático de utilizadores apenas pelo número de telemóvel;\n" +
                    "- Publicação e visualização de vídeos do YouTube e partilha de imagens em Hall Of Fame;\n" +
                    "- Apenas é guardada informação interagida dentro da app;\n" +
                    "- Salas de conversa dedicadas aos diferentes jogos com sistema de chamadas integrado;\n" +
                    "- Visualização e interação com todos os elementos registados na comunidade;\n" +
                    "- Possibilidade de alterar nome e avatar;\n" +
                    "- Possibilidade de LogOut;\n" +
                    "- Visualização de Perfil de utilizadores da comunidade;\n" +
                    "- Cada utilizador é obrigado a definir um nome após o registo;\n" +
                    "- Possibilidade de pedir uma funcionalidade ao developer; \n" +
                    "- Visualização de pagina info;\n" +
                    "- Visualização de utilizadores online;\n" +
                    "- Opção de remover conversas criados para o utilizador ou para os dois utilizadores;\n" +
                    "- Visualização de mensagem motivacional cada vez que a app é aberta, mensagem é completamente aleatório;\n" +
                    "- Espaço dedicado a links de download do jogo de cada sala criada;\n" +
                    "- Criação, visualização de salas privadas;\n" +
                    "- Eliminação de conta;\n" +
                    "- Opção de confirmação para sair da app;\n" +
                    "- Espaço com links para obter os jogos;\n" +
                    "- Opção de partilha da app com outros;\n" +
                    "- Partilhar de e para a app;\n" +
                    "\n" +
                    "ॐ Patronos ॐ\n" +
                    " \n" +
                    "-- Limite de 100mb no envio de ficheiros;\n" +
                    "\n" +
                    "\n" +
                    "ॐ Criadores de Sala Temática Privada ॐ\n" +
                    " \n" +
                    "- Alteração das cores da sala privada a gosto do criador da sala;\n" +
                    "- Bloqueio e desbloqueio de utilizadores na sala privada;\n" +
                    "- Aceitação de pedidos de acesso a sala privada;\n" +
                    "- Promoção e despromoção de admins da sala privada;\n" +
                    "- Eliminação de salas privadas criadas;\n" +
                    "- Limite de 15mb no envio de ficheiros;\n" +
                    "- Transferir posse de sala privada para outro utilizador registado na sala;\n" +
                    "- Alteração de nome e logo da sala;\n" +
                    "\n" +
                    "\n" +
                    "ॐ Admins Sala Temática Privada ॐ\n" +
                    "\n" +
                    "- Bloqueio e desbloqueio de utilizadores da sala privada;\n" +
                    "- Aceitação de pedidos de acesso a sala Privada;\n" +
                    "- Limite de 15mb no envio de ficheiros;\n" +
                    "\n" +
                    "ॐ Criadores de Sala Temática Pública ॐ\n" +
                    " \n" +
                    "- Alteração das cores da sala privada a gosto do criador da sala;\n" +
                    "- Bloqueio e desbloqueio de utilizadores na sala privada;\n" +
                    "- Promoção e despromoção de admins da sala privada;\n" +
                    "- Eliminação de salas privadas criadas;\n" +
                    "- Limite de 15mb no envio de ficheiros;\n" +
                    "- Transferir posse de sala privada para outro utilizador registado na sala;\n" +
                    "- Alteração de nome e logo da sala;\n" +
                    "\n" +
                    "\n" +
                    "ॐ Admins Sala Temática Pública ॐ\n" +
                    "\n" +
                    "- Bloqueio e desbloqueio de utilizadores da sala privada;\n" +
                    "- Limite de 15mb no envio de ficheiros;\n" +
                    "\n" +
                    "ॐ Admins Gerais ॐ\n" +
                    "\n" +
                    "- Possibilidade de notificar a comunidade de novo UpDate;\n" +
                    "- Envio de notificações personalizadas BroadCast;\n" +
                    "- Criação e eliminação de salas de jogos, novidades e Memes;\n" +
                    "- Visualização de Erros de todas as apps;\n" +
                    "- Possibilidade de bloquear e desbloquear utilizadores;\n" +
                    "- Por Cada novo registo cada admin recebe uma notificação a indicar o mesmo;\n" +
                    "- Sala privada;\n" +
                    "- Sem limite de tamanho no envio de ficheiros;\n" +
                    "- Registo de acesso a admins;\n" +
                    "- Notificações da sala privada apenas são entregues a Admins;\n" +
                    "- Envio BroadCast de mensagens motivacionais aleatórias;\n" +
                    "\n" +
                    "\n" +
                    "ॐ SuperAdmins ॐ\n" +
                    "\n" +
                    "- Possibilidade de eliminar salas de jogo;\n" +
                    "- Recebe notificação por cada Admin que entrar em Admins;\n" +
                    "- Recebe notificação por cada Admin que bloqueie um utilizador;\n" +
                    "\n" +
                    "\n" +
                    "ॐ Diferenças com outras aplicações do género ॐ\n" +
                    "\n" +
                    "- As chaves de encriptação são geradas no dispositivo e não em servidores ou seja, a chave não é gravada em mais nenhum lado sem ser nos dispositivos em que é devido;\n" +
                    "- Não tem publicidade;\n" +
                    "- Número de notificações bastante reduzido;\n" +
                    "- Salas privadas de qualquer tema totalmente encriptadas e com permissões de acesso;\n" +
                    "- Mensagens de motivação diárias para te dar um novo estado de espírito;\n" +
                    "- Sem recolha de dados de qualquer tipo;\n" +
                    "\n" +
                    "\n" +
                    "Comunidade ✶ Lua ॐ Negra ✶ Keep it Up ¯\\_(ツ)_/¯");
        }else {
            termosdeutilizacaoText.setText(getString(R.string.app_name) + " terms of use and community conditions" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "✶ Terms of use and conditions ✶\n" +
                    "\n" +
                    "- Lack of respect = Block;\n" +
                    "- Misuse of notifications is considered a lack of respect;\n" +
                    "- Disturbing background noise on the Call (eg: Television) is considered a lack of respect;\n" +
                    "- abusive use of profanity is considered a lack of respect;\n" +
                    "- User who is locked in 5 private rooms, gets blocked in the application;\n" +
                    "- User who has 5 warnings for inappropriate content in Hall Of Fame and YouTube = Block;\n" +
                    "- No pornographic content is allowed;\n" +
                    "- Fellowship, good attitude and good humor are key points;\n" +
                    "\n" +
                    "* The permissions requested to the user after the Sign In are used to send and receive files in the rooms and make calls, if they are not accepted the app should function normally without these features.\n" +
                    "* From user to admin and from admin to superadmin the features are inherited.\n" +
                    "* There is no advertising of any kind.\n" +
                    "* End-to-end encryption in private messages and in private rooms;\n" +
                    "* All information that is removed from the database is EVEN REMOVED NOT HAVING WAY TO RECOVER.\n" +
                    "* Application compatible with android versions higher than 7.0;\n" +
                    "* Application in development, may contain bugs;\n" +
                    "* Service to detect when users start the app and close the app;\n" +
                    "* We are not responsible for any loss of information in the application;\n" +
                    "* The patron's association can take from 24h to 48h;\n" +
                    "* Languages \u200B\u200B- EN and PT;\n" +
                    "\n" +
                    "\n" +
                    "ॐ  End-to-end encryption  ॐ\n" +
                    "\n" +
                    "- When creating chat generates a key in the phone -> Record the key in the phone -> sends by notification to the other user -> When receiving the notification it records the key in the phone and does not show the notification;\n" +
                    "- The key is not recorded on any other side other than the phone itself;\n" +
                    "\n" +
                    "\n" +
                    "ॐ  Thematic Rooms and Private Messages  ॐ\n" +
                    "\n" +
                    "- Each user can send a notification every 20 minutes;\n" +
                    "- Integrated call system with receipt of notification by each user entering the call;\n" +
                    "- Sending any file with size limit of 5mb\n" +
                    "- Use of a Service when starting the call and terminated when the call is disconnected;\n" +
                    "- Sending of memes;\n" +
                    "- Previews of links, videos, sounds and images;\n" +
                    "- Option to download the uploaded file;\n" +
                    "- Display of the last message received in private messages;\n" +
                    "- Visualization of users on call and in real time;\n" +
                    "\n" +
                    "\n" +
                    "ॐ  Charges  ॐ\n" +
                    "\n" +
                    "- Red -> superadmins;\n" +
                    "- Dark orange -> admins;\n" +
                    "- White -> users;\n" +
                    "- Golden → patrons;\n" +
                    "\n" +
                    "\n" +
                    "ॐ  Users  ॐ\n" +
                    "\n" +
                    "- Automatic registration of users only by mobile phone number;\n" +
                    "- Publishing and viewing YouTube videos and sharing images in Hall Of Fame;\n" +
                    "- Only interactive information is saved within the app;\n" +
                    "- Chat rooms dedicated to different games with integrated call system;\n" +
                    "- Visualization and interaction with all elements registered in the community;\n" +
                    "- Possibility of changing name and avatar;\n" +
                    "- Possibility of LogOut;\n" +
                    "- Profile view of community users;\n" +
                    "- Each user is required to define a name after registration;\n" +
                    "- Possibility to request a feature from developer;\n" +
                    "- Info page view;\n" +
                    "- View users online;\n" +
                    "- Option to remove conversations created for the user or for the two users;\n" +
                    "- Motivational message visualization every time the app is opened, message is completely random;\n" +
                    "- Space dedicated to game download links of each room created;\n" +
                    "- Creation, visualization of private rooms;\n" +
                    "- Account deletion;\n" +
                    "- Confirmation option to exit the app;\n" +
                    "- Space with links to get the games;\n" +
                    "- Option to share the app with others;\n" +
                    "- Share to and from the app;\n" +
                    "\n" +
                    "ॐ  Patrons  ॐ\n" +
                    " \n" +
                    "- Limit of 100mb in sending files;\n" +
                    "\n" +
                    "\n" +
                    "ॐ  Thematic Rooms  ॐ\n" +
                    " \n" +
                    "- Changing the colors of the private room to the taste of the creator of the room;\n" +
                    "- Blocking and unblocking users in the private room;\n" +
                    "- Acceptance of requests for access to private room;\n" +
                    "- Promotion and relegation of private room admins;\n" +
                    "- Elimination of private rooms created;\n" +
                    "- 15mb limit on sending files;\n" +
                    "- Transfer ownership of a private room to another registered user in the room;\n" +
                    "- Change of name and logo of the room;\n" +
                    "\n" +
                    "\n" +
                    " ॐ  Private Thematic Room Admins  ॐ\n" +
                    "\n" +
                    "- Blocking and unblocking private room users;\n" +
                    "- Acceptance of requests for access to the Private Room;\n" +
                    "- 15mb limit on sending files;\n" +
                    "\n" +
                    "ॐ  Public Thematic Room Creators  ॐ\n" +
                    " \n" +
                    "- Changing the colors of the private room to the taste of the creator of the room;\n" +
                    "- Blocking and unblocking users in the private room;\n" +
                    "- Promotion and relegation of private room admins;\n" +
                    "- Elimination of private rooms created;\n" +
                    "- 15mb limit on sending files;\n" +
                    "- Transfer ownership of a private room to another registered user in the room;\n" +
                    "- Change of name and logo of the room;\n" +
                    "\n" +
                    "ॐ  Thematic Public Room Admins  ॐ\n" +
                    "\n" +
                    "- Blocking and unblocking private room users;\n" +
                    "- 15mb limit on sending files;\n" +
                    "\n" +
                    "ॐ  General Admins  ॐ\n" +
                    "\n" +
                    "- Possibility to notify the community of new UpDate;\n" +
                    "- Sending personalized BroadCast notifications;\n" +
                    "- Creation and elimination of play rooms, news and Memes;\n" +
                    "- Bug view of all apps;\n" +
                    "- Possibility of blocking and unblocking users;\n" +
                    "- For each new registration each admin receives a notification to indicate the same;\n" +
                    "- Private room;\n" +
                    "- No size limit on sending files;\n" +
                    "- Registration of access to admins;\n" +
                    "- Private room notifications are only delivered to Admins;\n" +
                    "- Broadcast sending of random motivational messages;\n" +
                    "\n" +
                    "\n" +
                    "ॐ  SuperAdmins  ॐ\n" +
                    "\n" +
                    "- Possibility of eliminating game rooms;\n" +
                    "- Receive notification by each Admin who logs into Admins;\n" +
                    "- Receive notification by each Admin that blocks a user;\n" +
                    "\n" +
                    "\n" +
                    "ॐ  Differences with other applications of the genre  ॐ\n" +
                    "\n" +
                    "- Encryption keys are generated on the device and not on servers ie the key is not written on any side other than on the devices where it is due;\n" +
                    "- It has no advertising;\n" +
                    "- very low number of notifications;\n" +
                    "- Private rooms of any theme totally encrypted and with access permissions;\n" +
                    "- Daily motivational messages to give you a new state of mind;\n" +
                    "- No data collection of any kind;\n" +
                    "\n" +
                    "\n" +
                    "Community ✶ Lua ॐ Negra ✶ Keep it Up ¯ \\ _ (ツ) _ / ¯");
        }

    }
}
