#!/usr/bin/env python3
import http.server
import socketserver
import os
from datetime import datetime
import pytz
#print('source code for "http.server":', http.server.__file__)

class web_server(http.server.SimpleHTTPRequestHandler):
    
    def do_GET(self):

        print(self.path)
        
        if self.path == '/':
            self.protocol_version = 'HTTP/1.1'
            self.send_response(200)
            self.send_header("Content-type", "text/html; charset=UTF-8")
            self.end_headers()            
            
            text = b"Hello World!\n"
            self.wfile.write(text)
        elif self.path.startswith('/?cmd='):
            param = self.path[6:]
            self.protocol_version = 'HTTP/1.1'
            self.send_response(200)
            self.send_header("Content-type", "text/html; charset=UTF-8")
            self.end_headers()    
            if param=='time':
                warsawTimeZone = pytz.timezone("Europe/Warsaw") 
                warsawTime = datetime.now(warsawTimeZone)
                text = (warsawTime.strftime('%H:%M:%S')+"\n").encode()
                self.wfile.write(text)
            elif param.startswith('rev'):
                param2 = param.removeprefix('rev')
                if param2.startswith('&str='):
                    param2 =param2.removeprefix('&str=')
                    if param2.__len__ == 0:
                        self.wfile.write(b"wrong command\n")
                    else:
                        text = (param2[::-1]+'\n').encode()
                        self.wfile.write(text)
                else:
                    self.wfile.write(b"wrong command\n")
            else:
                self.wfile.write(b"wrong command\n")
        else:
            super().do_GET()
    
# --- main ---

PORT = 4080

print(f'Starting: http://localhost:{PORT}')

tcp_server = socketserver.TCPServer(("",PORT), web_server)
tcp_server.serve_forever()
