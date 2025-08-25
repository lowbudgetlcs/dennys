# pyright: basic

from argparse import ArgumentParser


def args(parser: ArgumentParser):
    parser.add_argument(
        "-s",
        action="store_true",
        default=False,
        help="Toggles the 'https' protocol (default '%(default)s').",
    )
    parser.add_argument(
        "-e",
        "--environment",
        default="",
        help="The environment to target (default: '%(default)s').",
    )
    parser.add_argument(
        "-d",
        "--domain",
        default="localhost:9292",
        help="The domain to target (default: '%(default)s').",
    )
    return parser


def base(parser: ArgumentParser):
    args = parser.parse_args()
    environment = args.environment
    domain = args.domain
    protocol = "http"
    if args.s:
        protocol = "https"
    if environment != "":
        environment = f"{environment}."
    return f"{protocol}://{environment}{domain}/api/v1"
