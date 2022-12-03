<p><span style="font-size: 36px; color: #ff0000;">ALPHA BUILD</span></p>
<p>&nbsp;</p>
<p>This block allows for creative flight in the intimidate local area around it. By default, this is 32 blocks, but can be configured below<br />It consumes fuel to do this (nether stars by default) and each star gives 5 mins of flight by default</p>
<p><br /><strong>Note: only dimensions in the whitelist will allow the controller to be placed</strong></p>
<p>&nbsp;</p>
<p><span style="font-size: 18px;">usage:</span><br />to fuel, right-click a nether start into block. It can hold up to a stack<br />shift + right click the block to turn on/off<br />fuel is not consumed while it is off and the timer pauses</p>
<p>&nbsp;</p>
<p><span style="font-size: 24px;"><span style="font-size: 18px;">recipe:</span></span><br /><img src="https://i.imgur.com/uFcJKlv.png" width="492" height="254" /></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p><span style="font-size: 18px;">default config:</span></p>
<p>&nbsp;</p>
<pre style="background-color: #2b2b2b; color: #a9b7c6; font-family: 'JetBrains Mono',monospace; font-size: 9.8pt;"><br /><span style="color: #808080;">#Config for local flight controller<br /></span>[<span style="color: #cc7832;">General</span>]<br />   <span style="color: #808080;">#dimensions allowed (mod:dimension_name)<br /></span>   <span style="color: #cc7832;">dimensionWhitelist </span>= [<span style="color: #6a8759;">"minecraft:overworld"</span>, <span style="color: #6a8759;">"minecraft:the_nether"</span>, <span style="color: #6a8759;">"minecraft:the_end"</span>]<br />   <span style="color: #808080;">#fuel to power FlightController (mod:item)<br /></span>   <span style="color: #cc7832;">fuelType </span>= <span style="color: #6a8759;">"minecraft:nether_star"<br /></span>   <span style="color: #808080;">#The number of ticks a single fuel unit provides (1 seconds = 20 ticks)<br /></span><span style="color: #808080;">   #Range: 20 ~ 72000<br /></span>   <span style="color: #cc7832;">fuelTime </span>= <span style="color: #6897bb;">6000<br /></span>   <span style="color: #808080;">#Radius of effect (in blocks)<br /></span><span style="color: #808080;">   #Range: 5 ~ 96<br /></span>   <span style="color: #cc7832;">range </span>= <span style="color: #6897bb;">32<br /></span>   <span style="color: #808080;">#Determines if Flight Controller needs to burn fuel to work<br /></span>   <span style="color: #cc7832;">requiresFuel </span>= true<br /><br /></pre>
<p>&nbsp;</p>