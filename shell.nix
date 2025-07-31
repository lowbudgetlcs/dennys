{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
    buildInputs = with pkgs; [
      jdk21 gnumake
      go gopls
      yaml-language-server
    ];
    env = {
      TEST="Hello there...";
      PROJ_HOME="~/code/dev/lblcs/dennys";
    };
    shellHook = ''
      alias home="cd $PROJ_HOME"
      cowsay $TEST
    '';
}
